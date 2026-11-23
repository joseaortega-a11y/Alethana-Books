<?php
namespace Alethana\Modelo;

class Venta
{
    private \DateTimeImmutable $fecha;
    private float $subtotal;
    private float $descuento;
    private string $tipoDescuento = 'NORMAL';   // NORMAL, PROMOCIONAL, DIGITAL, FIJO
    private ?string $codigo = null;             // código de descuento aplicado

    /** @param DetalleVenta[] $detalles */
    public function __construct(
        private ?string $id,
        private Usuario $usuario,
        private array $detalles,
        private string $metodoPago,
        private float $total,
        private string $estadoPago,
        private DatosEnvio $datosEnvio
    ) {
        $this->fecha     = new \DateTimeImmutable();
        $this->subtotal  = round(array_sum(array_map(fn(DetalleVenta $d) => $d->getSubtotal(), $detalles)), 2);
        $this->descuento = max(0, round($this->subtotal - $total, 2));
    }

    public function getId(): ?string               { return $this->id; }
    public function getUsuario(): Usuario          { return $this->usuario; }
    public function getFecha(): \DateTimeImmutable { return $this->fecha; }
    /** @return DetalleVenta[] */
    public function getDetalles(): array           { return $this->detalles; }
    public function getTotal(): float              { return $this->total; }
    public function getMetodoPago(): string        { return $this->metodoPago; }
    public function getEstadoPago(): string        { return $this->estadoPago; }
    public function getDatosEnvio(): DatosEnvio    { return $this->datosEnvio; }
    public function getSubtotal(): float           { return $this->subtotal; }
    public function getDescuento(): float          { return $this->descuento; }
    public function getTipoDescuento(): string     { return $this->tipoDescuento; }
    public function getCodigo(): ?string           { return $this->codigo; }

    public function agregarDetalle(DetalleVenta $d): void     { $this->detalles[] = $d; }
    public function setId(?string $id): void                  { $this->id = $id; }
    public function setFecha(\DateTimeImmutable $fecha): void { $this->fecha = $fecha; }
    public function setSubtotal(float $subtotal): void        { $this->subtotal = $subtotal; }
    public function setDescuento(float $descuento): void      { $this->descuento = $descuento; }
    public function setTipoDescuento(string $tipo): void      { $this->tipoDescuento = $tipo; }
    public function setCodigo(?string $codigo): void          { $this->codigo = $codigo; }
}
