<?php
/** Panel de administración: CRUD de libros y pedidos (equivalente a admin.fxml + AdminController). */
require __DIR__ . '/../bootstrap.php';
require __DIR__ . '/../templates/componentes.php';

use Alethana\Factory\LibroFactory;
use Alethana\Interfaces\Validable;
use Alethana\Modelo\Libro;
use Alethana\Modelo\LibroDigital;
use Alethana\Modelo\LibroFisico;
use Alethana\Persistence\VentaRepository;
use Alethana\Service\CatalogoService;
use Alethana\Util\Sesion;

$admin    = Sesion::requerirAdmin();
$catalogo = new CatalogoService();
$config   = require __DIR__ . '/../config/config.php';

/**
 * Guarda un archivo subido y devuelve su ruta relativa (ej. "portadas/mi_libro.jpg"), o null si no se subió nada.
 * @param string[] $extensiones
 */
function guardarSubida(string $campo, array $extensiones, string $carpetaBase, string $subcarpeta): ?string
{
    $f = $_FILES[$campo] ?? null;
    if ($f === null || $f['error'] === UPLOAD_ERR_NO_FILE) return null;
    if ($f['error'] !== UPLOAD_ERR_OK) throw new RuntimeException('No se pudo subir el archivo (código ' . $f['error'] . ').');

    $ext = strtolower(pathinfo($f['name'], PATHINFO_EXTENSION));
    if (!in_array($ext, $extensiones, true)) {
        throw new RuntimeException('Tipo de archivo no permitido. Usa: ' . implode(', ', $extensiones));
    }
    // Nombre seguro: solo letras, números, guiones y guion bajo
    $base   = preg_replace('/[^a-zA-Z0-9_-]+/', '_', pathinfo($f['name'], PATHINFO_FILENAME)) ?: 'archivo';
    $nombre = $subcarpeta . '/' . $base . '.' . $ext;
    $destino = $carpetaBase . $nombre;
    if (!is_dir(dirname($destino))) mkdir(dirname($destino), 0775, true);
    if (!move_uploaded_file($f['tmp_name'], $destino)) throw new RuntimeException('No se pudo guardar el archivo subido.');
    return $nombre;
}

if (esPost()) {
    verificarCsrf();
    $accion = $_POST['accion'] ?? '';

    if ($accion === 'eliminar') {
        $libro = $catalogo->buscarPorId((string) ($_POST['id'] ?? ''));
        if ($libro) {
            $catalogo->eliminar($libro->getId());
            flash('ok', "«{$libro->getTitulo()}» eliminado del catálogo.");
        }
        redirigir('admin.php');
    }

    if ($accion === 'guardar') {
        $id        = trim($_POST['id'] ?? '');
        $titulo    = trim($_POST['titulo'] ?? '');
        $autor     = trim($_POST['autor'] ?? '');
        $categoria = trim($_POST['categoria'] ?? '');
        $tipo      = ($_POST['tipo'] ?? '') === 'DIGITAL' ? LibroFactory::DIGITAL : LibroFactory::FISICO;
        $formato   = in_array($_POST['formato'] ?? '', ['PDF', 'EPUB', 'MOBI'], true) ? $_POST['formato'] : 'PDF';
        $origen    = ($_POST['origen'] ?? '') === 'Importado' ? 'Importado' : 'Nacional';
        $precioTxt = trim($_POST['precio'] ?? '');
        $stockTxt  = trim($_POST['stock'] ?? '');
        $volver    = 'admin.php' . ($id !== '' ? '?editar=' . urlencode($id) : '');

        try {
            if ($titulo === '' || $autor === '' || !in_array($categoria, $catalogo->obtenerCategorias(), true)) {
                throw new RuntimeException('Título, autor, categoría y tipo son obligatorios.');
            }
            if (!is_numeric($precioTxt) || !preg_match('/^-?\d+$/', $stockTxt)) {
                throw new RuntimeException('Precio y stock deben ser números válidos. Precio: usa punto decimal (ej: 29000.0). Stock: solo números enteros.');
            }
            $precio = (float) $precioTxt;
            $stock  = (int) $stockTxt;
            if ($precio <= 0) throw new RuntimeException('El precio debe ser mayor a cero.');
            if ($stock < 0)   throw new RuntimeException('El stock no puede ser negativo.');

            $imagen  = guardarSubida('imagen', ['png', 'jpg', 'jpeg', 'webp'], $config['carpeta_imagenes'], 'portadas');
            $archivo = $tipo === LibroFactory::DIGITAL
                ? guardarSubida('archivo', ['pdf', 'epub', 'mobi'], $config['carpeta_descargables'], 'archivos')
                : null;

            if ($id === '') {
                // Nuevo libro: el Factory decide la subclase; el id lo genera la BD
                $nuevo = LibroFactory::crearLibro($tipo, null, $titulo, $autor, $categoria, $precio, $stock,
                                                  $imagen, $formato, $archivo, $origen);
                if ($nuevo instanceof Validable && !$nuevo->esValido()) {
                    throw new RuntimeException($nuevo->obtenerMensajeError());
                }
                $catalogo->agregar($nuevo);
                flash('ok', "«{$titulo}» agregado al catálogo.");
                redirigir('admin.php');
            }

            $libro = $catalogo->buscarPorId($id) ?? throw new RuntimeException('El libro ya no existe.');
            $libro->setTitulo($titulo);
            $libro->setAutor($autor);
            $libro->setCategoria($categoria);
            $libro->setPrecio($precio);
            $libro->setStock($stock);
            if ($imagen !== null) $libro->setImagen($imagen);
            if ($libro instanceof LibroDigital) {
                if ($archivo !== null) $libro->setRutaArchivo($archivo);
                $libro->setFormato($formato);
                $libro->setDisponibleDescarga(trim((string) $libro->getRutaArchivo()) !== '');
            } elseif ($libro instanceof LibroFisico) {
                $libro->setOrigen($origen);
            }
            $catalogo->actualizar($libro);
            flash('ok', "«{$titulo}» actualizado.");
            redirigir('admin.php');
        } catch (RuntimeException $e) {
            flash('error', $e->getMessage());
            redirigir($volver);
        }
    }
    redirigir('admin.php');
}

$busqueda   = trim($_GET['q'] ?? '');
$libros     = $catalogo->buscar($busqueda);
$categorias = $catalogo->obtenerCategorias();
$enEdicion  = isset($_GET['editar']) ? $catalogo->buscarPorId((string) $_GET['editar']) : null;
$tipoForm   = $enEdicion instanceof LibroDigital ? 'DIGITAL' : 'FISICO';

$ventas   = (new VentaRepository())->obtenerTodas();
$hoy      = (new DateTimeImmutable())->format('Y-m-d');
$ventasHoy = array_sum(array_map(fn($v) => $v->getFecha()->format('Y-m-d') === $hoy ? $v->getTotal() : 0, $ventas));
$pestana  = ($_GET['pestana'] ?? '') === 'pedidos' ? 'pedidos' : 'libros';

$tipoTexto = fn(Libro $l) => $l instanceof LibroDigital ? 'Digital' : 'Físico';
$origenTexto = fn(Libro $l) => $l instanceof LibroFisico ? $l->getOrigen() : '—';
?>
<!doctype html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Administración · Alethana Books</title>
    <link rel="stylesheet" href="css/estilos.css">
</head>
<body>
<header class="topbar">
    <div class="topbar__fila">
        <a class="logo" href="admin.php">Alethana <span>Books</span> · Admin</a>
        <nav class="acciones">
            <span class="accion">👤 <?= e($admin->getNombre()) ?></span>
            <a href="index.php" class="accion">Ver tienda</a>
            <a href="logout.php" class="accion accion--salir">Cerrar sesión</a>
        </nav>
    </div>
    <nav class="menu">
        <a href="admin.php" class="<?= $pestana === 'libros' ? 'activo' : '' ?>">📚 Libros</a>
        <a href="admin.php?pestana=pedidos" class="<?= $pestana === 'pedidos' ? 'activo' : '' ?>">📦 Pedidos (<?= count($ventas) ?>)</a>
    </nav>
</header>

<main class="contenido">
<?php foreach (obtenerFlashes() as $f): ?>
    <div class="aviso aviso--<?= e($f['tipo']) ?>"><?= e($f['mensaje']) ?></div>
<?php endforeach; ?>

<?php if ($pestana === 'libros'): ?>
<div class="admin">
    <form method="post" enctype="multipart/form-data" class="panel formulario" id="form-libro">
        <?= campoCsrf() ?>
        <input type="hidden" name="accion" value="guardar">
        <input type="hidden" name="id" value="<?= e($enEdicion?->getId()) ?>">
        <h2><?= $enEdicion ? 'Modificar libro #' . e($enEdicion->getId()) : 'Agregar libro' ?></h2>

        <label>Título <input name="titulo" maxlength="150" value="<?= e($enEdicion?->getTitulo()) ?>" required></label>
        <label>Autor <input name="autor" maxlength="100" value="<?= e($enEdicion?->getAutor()) ?>" required></label>
        <label>Categoría
            <select name="categoria" required>
                <option value="">Seleccionar categoría</option>
                <?php foreach ($categorias as $c): ?>
                    <option <?= $enEdicion?->getCategoria() === $c ? 'selected' : '' ?>><?= e($c) ?></option>
                <?php endforeach; ?>
            </select>
        </label>
        <div class="fila">
            <label>Precio (COP) <input name="precio" type="number" min="1" step="0.01" value="<?= $enEdicion ? (int) $enEdicion->getPrecio() : '' ?>" required></label>
            <label>Stock <input name="stock" type="number" min="0" step="1" value="<?= $enEdicion?->getStock() ?>" required></label>
        </div>
        <label>Tipo
            <select name="tipo" id="campo-tipo" <?= $enEdicion ? 'disabled' : '' ?>>
                <option value="FISICO" <?= $tipoForm === 'FISICO' ? 'selected' : '' ?>>Físico</option>
                <option value="DIGITAL" <?= $tipoForm === 'DIGITAL' ? 'selected' : '' ?>>Digital</option>
            </select>
            <?php if ($enEdicion): ?><input type="hidden" name="tipo" value="<?= $tipoForm ?>"><?php endif; ?>
        </label>
        <label data-solo="FISICO">Origen
            <select name="origen">
                <?php foreach (['Nacional', 'Importado'] as $o): ?>
                    <option <?= ($enEdicion instanceof LibroFisico && $enEdicion->getOrigen() === $o) ? 'selected' : '' ?>><?= $o ?></option>
                <?php endforeach; ?>
            </select>
        </label>
        <label data-solo="DIGITAL">Formato
            <select name="formato">
                <?php foreach (['PDF', 'EPUB', 'MOBI'] as $fmt): ?>
                    <option <?= ($enEdicion instanceof LibroDigital && $enEdicion->getFormato() === $fmt) ? 'selected' : '' ?>><?= $fmt ?></option>
                <?php endforeach; ?>
            </select>
        </label>
        <label data-solo="DIGITAL">Archivo descargable (PDF, EPUB, MOBI)
            <input type="file" name="archivo" accept=".pdf,.epub,.mobi">
            <small><?= $enEdicion instanceof LibroDigital && $enEdicion->getRutaArchivo() ? 'Actual: ' . e(basename($enEdicion->getRutaArchivo())) : 'Sin archivo seleccionado' ?></small>
        </label>
        <label>Portada (PNG, JPG, WEBP)
            <input type="file" name="imagen" accept=".png,.jpg,.jpeg,.webp">
            <small><?= $enEdicion?->getImagen() ? 'Actual: ' . e($enEdicion->getImagen()) : 'Sin imagen seleccionada' ?></small>
        </label>

        <div class="fila">
            <button class="btn"><?= $enEdicion ? '💾 Modificar' : '+ Agregar' ?></button>
            <?php if ($enEdicion): ?>
                <a href="admin.php" class="btn btn--secundario">Cancelar edición</a>
            <?php endif; ?>
        </div>
    </form>

    <section>
        <form method="get" class="buscador buscador--admin">
            <input type="search" name="q" placeholder="Buscar por título, autor o categoría" value="<?= e($busqueda) ?>">
            <button>Buscar</button>
        </form>
        <div class="tabla-contenedor">
            <table class="tabla">
                <thead>
                    <tr><th>ID</th><th>Portada</th><th>Título</th><th>Autor</th><th>Categoría</th><th>Tipo</th><th>Origen</th><th>Precio</th><th>Stock</th><th></th></tr>
                </thead>
                <tbody>
                <?php foreach ($libros as $l): ?>
                    <tr class="<?= $enEdicion?->getId() === $l->getId() ? 'seleccionada' : '' ?>">
                        <td><?= e($l->getId()) ?></td>
                        <td><?= portada($l, 'portada portada--tabla') ?></td>
                        <td><?= e($l->getTitulo()) ?></td>
                        <td><?= e($l->getAutor()) ?></td>
                        <td><?= e($l->getCategoria()) ?></td>
                        <td><?= $tipoTexto($l) ?></td>
                        <td><?= e($origenTexto($l)) ?></td>
                        <td><?= cop($l->getPrecio()) ?></td>
                        <td><?= $l->getStock() ?></td>
                        <td class="tabla__acciones">
                            <a href="admin.php?editar=<?= e($l->getId()) ?>" class="btn btn--chico">Editar</a>
                            <form method="post" data-confirmar="¿Eliminar «<?= e($l->getTitulo()) ?>»?">
                                <?= campoCsrf() ?>
                                <input type="hidden" name="accion" value="eliminar">
                                <input type="hidden" name="id" value="<?= e($l->getId()) ?>">
                                <button class="btn btn--chico btn--peligro">Eliminar</button>
                            </form>
                        </td>
                    </tr>
                <?php endforeach; ?>
                <?php if ($libros === []): ?>
                    <tr><td colspan="10" class="vacio">No hay libros que coincidan.</td></tr>
                <?php endif; ?>
                </tbody>
            </table>
        </div>
    </section>
</div>

<?php else: ?>
    <p class="resumen-pedidos">Pedidos totales: <b><?= count($ventas) ?></b> | Ventas hoy: <b><?= cop($ventasHoy) ?></b></p>
    <?php if ($ventas === []): ?><p class="vacio">No hay pedidos registrados aún.</p><?php endif; ?>
    <div class="grilla-pedidos">
    <?php foreach ($ventas as $v): $envio = $v->getDatosEnvio(); ?>
        <article class="panel pedido">
            <header>
                <div>
                    <strong><?= e($v->getUsuario()->getNombre()) ?></strong> · <?= e($v->getUsuario()->getCorreo()) ?><br>
                    <small>Pedido #<?= e($v->getId()) ?> · <?= $v->getFecha()->format('d/m/Y H:i') ?> · <?= e($v->getMetodoPago()) ?></small>
                </div>
                <span class="chip chip--verde"><?= e($v->getEstadoPago()) ?></span>
            </header>
            <ul>
            <?php foreach ($v->getDetalles() as $d): ?>
                <li><span><?= e($d->getLibro()->getTitulo()) ?> ×<?= $d->getCantidad() ?></span><span><?= cop($d->getSubtotal()) ?></span></li>
            <?php endforeach; ?>
            </ul>
            <?php if ($envio->estaCompleto()): ?>
                <p class="tarjeta__autor"><b>Datos de envío:</b> <?= e($envio->getNombre()) ?> · <?= e($envio->getTelefono()) ?><br>
                   <?= e($envio->getDireccion()) ?>, <?= e($envio->getCiudad()) ?></p>
            <?php endif; ?>
            <?php if ($v->getDescuento() > 0): ?>
                <p class="nota-verde">Descuento <?= e(strtolower($v->getTipoDescuento())) ?><?= $v->getCodigo() ? ' (' . e($v->getCodigo()) . ')' : '' ?>: - <?= cop($v->getDescuento()) ?></p>
            <?php endif; ?>
            <p class="pedido__total"><span>TOTAL</span><span><?= cop($v->getTotal()) ?></span></p>
        </article>
    <?php endforeach; ?>
    </div>
<?php endif; ?>
</main>
<script src="js/app.js"></script>
</body>
</html>
