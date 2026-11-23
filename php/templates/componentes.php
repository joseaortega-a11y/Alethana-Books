<?php
/** Componentes reutilizables (equivalente a ImagenUtil y TarjetaUtil de la versión Java). */

use Alethana\Modelo\Libro;
use Alethana\Modelo\LibroDigital;
use Alethana\Modelo\LibroFisico;

/** Portada del libro o un marcador si no hay imagen. */
function portada(Libro $libro, string $clase = 'portada'): string
{
    $url = urlImagen($libro->getImagen());
    if ($url === null) {
        return '<div class="' . $clase . ' portada--vacia" aria-hidden="true">📚</div>';
    }
    return '<img class="' . $clase . '" src="' . e($url) . '" alt="Portada de ' . e($libro->getTitulo()) . '" loading="lazy">';
}

/** Formulario "Agregar al carrito" que vuelve a la página actual. */
function botonCarrito(Libro $libro, string $clase = 'btn btn--bloque'): string
{
    if (!$libro->hayStock()) {
        return '<button class="' . $clase . '" disabled>Sin stock</button>';
    }
    return '<form method="post" action="carrito.php">'
         . campoCsrf()
         . '<input type="hidden" name="accion" value="agregar">'
         . '<input type="hidden" name="id_libro" value="' . e($libro->getId()) . '">'
         . '<input type="hidden" name="volver" value="' . e(urlActual()) . '">'
         . '<button type="submit" class="' . $clase . '">Agregar al carrito</button>'
         . '</form>';
}

/**
 * Tarjeta de libro para las grillas del catálogo.
 * Opciones: etiqueta (texto de badge extra), precioDigital (bool: muestra el −10% de e-books).
 */
function tarjetaLibro(Libro $libro, array $opciones = []): string
{
    ob_start(); ?>
    <article class="tarjeta">
        <div class="tarjeta__imagen">
            <?= portada($libro) ?>
            <?php if ($libro instanceof LibroDigital): ?>
                <span class="badge badge--digital">⬇ Digital</span>
                <?php if (!empty($opciones['precioDigital'])): ?>
                    <span class="badge badge--formato">📄 <?= e($libro->getFormato()) ?></span>
                <?php endif; ?>
            <?php endif; ?>
            <?php if ($libro instanceof LibroFisico && $libro->esImportado()): ?>
                <span class="badge badge--importado"><?= e($opciones['etiqueta'] ?? 'Importado') ?></span>
            <?php endif; ?>
        </div>
        <h3 class="tarjeta__titulo" title="<?= e($libro->getTitulo()) ?>"><?= e($libro->getTitulo()) ?></h3>
        <p class="tarjeta__autor"><?= e($libro->getAutor()) ?></p>
        <span class="chip"><?= e($libro->getCategoria()) ?></span>
        <?php if (!empty($opciones['precioDigital'])): ?>
            <p class="precio">
                <s class="precio__antes"><?= cop($libro->getPrecio()) ?></s>
                <?= cop($libro->getPrecio() * 0.9) ?>
            </p>
            <p class="nota-verde">−10% e-book · ⚡ Descarga instantánea</p>
        <?php else: ?>
            <p class="precio"><?= cop($libro->getPrecio()) ?></p>
            <p class="stock <?= $libro->hayStock() ? 'stock--ok' : 'stock--no' ?>">
                <?= $libro->hayStock() ? 'Stock: ' . $libro->getStock() : 'Sin stock' ?>
            </p>
        <?php endif; ?>
        <?= botonCarrito($libro) ?>
    </article>
    <?php return ob_get_clean();
}

/** @param Libro[] $libros */
function grillaLibros(array $libros, string $mensajeVacio, array $opciones = [], string $clase = 'grilla'): string
{
    if ($libros === []) {
        return '<p class="vacio">' . e($mensajeVacio) . '</p>';
    }
    return '<div class="' . $clase . '">' . implode('', array_map(fn($l) => tarjetaLibro($l, $opciones), $libros)) . '</div>';
}
