<?php
namespace Alethana\Service;

use Alethana\Modelo\Carrito;
use Alethana\Persistence\CodigoDescuentoRepository;
use Alethana\Strategy\DescuentoDigital;
use Alethana\Strategy\DescuentoNormal;
use Alethana\Strategy\DescuentoPromocional;
use Alethana\Strategy\EstrategiaDescuento;

/**
 * El carrito vive en la sesión PHP como [idLibro => cantidad].
 * En cada petición se reconstruye con precios y stock actuales de la BD.
 */
class CarritoService
{
    private CatalogoService $catalogo;

    public function __construct()
    {
        $this->catalogo = new CatalogoService();
        $_SESSION['carrito'] ??= [];
    }

    public function agregarLibro(string $idLibro, int $cantidad = 1): string
    {
        $libro = $this->catalogo->buscarPorId($idLibro);
        if ($libro === null) throw new \InvalidArgumentException('El libro no existe.');

        // Validación con el modelo (lanza excepción si no hay stock suficiente)
        $this->getCarrito()->agregarLibro($libro, $cantidad);

        $_SESSION['carrito'][$idLibro] = ($_SESSION['carrito'][$idLibro] ?? 0) + $cantidad;
        return $libro->getTitulo();
    }

    public function eliminarLibro(string $idLibro): void
    {
        unset($_SESSION['carrito'][$idLibro]);
    }

    public function vaciarCarrito(): void
    {
        $_SESSION['carrito'] = [];
        unset($_SESSION['codigo_descuento']);
    }

    public function contarItems(): int
    {
        return array_sum($_SESSION['carrito'] ?? []);
    }

    public function getCarrito(): Carrito
    {
        $carrito = new Carrito();
        foreach ($_SESSION['carrito'] as $id => $cantidad) {
            $libro = $this->catalogo->buscarPorId((string) $id);
            if ($libro === null) {               // el libro fue eliminado por el admin
                unset($_SESSION['carrito'][$id]);
                continue;
            }
            // Se agrega sin validar stock para que el usuario vea el carrito; el stock se valida al pagar
            $carrito->agregarLibroSinValidar($libro, (int) $cantidad);
        }
        return $carrito;
    }

    /** Aplica (o quita) un código promocional. Devuelve el porcentaje o null si no es válido. */
    public function aplicarCodigo(string $codigo): ?int
    {
        $codigo = strtoupper(trim($codigo));
        $porcentaje = $codigo === '' ? null : (new CodigoDescuentoRepository())->buscarPorcentaje($codigo);
        if ($porcentaje === null) {
            unset($_SESSION['codigo_descuento']);
        } else {
            $_SESSION['codigo_descuento'] = $codigo;
        }
        return $porcentaje;
    }

    public function getCodigoAplicado(): ?string
    {
        return $_SESSION['codigo_descuento'] ?? null;
    }

    /**
     * Strategy: elige la estrategia de descuento.
     * Código promocional > descuento digital automático (si hay e-books) > sin descuento.
     */
    public function obtenerEstrategia(Carrito $carrito): EstrategiaDescuento
    {
        $codigo = $this->getCodigoAplicado();
        if ($codigo !== null) {
            $porcentaje = (new CodigoDescuentoRepository())->buscarPorcentaje($codigo);
            if ($porcentaje !== null) return new DescuentoPromocional($porcentaje / 100, $codigo);
        }
        if ($carrito->tieneDigitales()) return new DescuentoDigital($carrito->getItems());
        return new DescuentoNormal();
    }
}
