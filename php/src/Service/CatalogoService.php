<?php
namespace Alethana\Service;

use Alethana\Modelo\Libro;
use Alethana\Modelo\LibroDigital;
use Alethana\Modelo\LibroFisico;
use Alethana\Persistence\LibroRepository;

class CatalogoService
{
    private LibroRepository $libroRepository;

    public function __construct()
    {
        $this->libroRepository = new LibroRepository();
    }

    /** @return Libro[] */
    public function obtenerTodos(): array            { return $this->libroRepository->obtenerTodos(); }
    public function buscarPorId(string $id): ?Libro  { return $this->libroRepository->buscarPorId($id); }
    /** @return string[] */
    public function obtenerCategorias(): array       { return $this->libroRepository->obtenerCategorias(); }

    /**
     * Filtra con una función (equivalente a la interfaz funcional FiltroLibro de Java).
     * @param callable(Libro): bool $filtro
     * @return Libro[]
     */
    public function filtrar(callable $filtro): array
    {
        return array_values(array_filter($this->obtenerTodos(), $filtro));
    }

    /** @return Libro[] */
    public function buscar(string $texto): array
    {
        return $this->filtrar(fn(Libro $l) => $l->coincideCon($texto));
    }

    /** @return Libro[] */
    public function filtrarPorCategoria(string $categoria): array
    {
        return $this->filtrar(fn(Libro $l) => mb_strtolower($l->getCategoria()) === mb_strtolower($categoria));
    }

    /** @return LibroFisico[] */
    public function obtenerImportados(): array
    {
        return $this->filtrar(fn(Libro $l) => $l instanceof LibroFisico && $l->esImportado());
    }

    /** @return LibroDigital[] */
    public function obtenerDigitales(?string $formato = null): array
    {
        return $this->filtrar(fn(Libro $l) => $l instanceof LibroDigital
            && $l->estaDisponibleParaDescarga()
            && ($formato === null || strcasecmp((string) $l->getFormato(), $formato) === 0));
    }

    /** @return Libro[] */
    public function obtenerRecomendadosAleatorios(int $limite): array
    {
        $libros = $this->obtenerTodos();
        shuffle($libros);
        return array_slice($libros, 0, $limite);
    }

    /** @return Libro[] los más recientes primero */
    public function obtenerUltimos(int $limite): array
    {
        return array_reverse(array_slice($this->obtenerTodos(), -$limite));
    }

    public function agregar(Libro $libro): void    { $this->libroRepository->agregar($libro); }
    public function actualizar(Libro $libro): void { $this->libroRepository->actualizar($libro); }
    public function eliminar(string $id): void     { $this->libroRepository->eliminar($id); }
}
