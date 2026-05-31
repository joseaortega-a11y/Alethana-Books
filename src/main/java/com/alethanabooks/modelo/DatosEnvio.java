package com.alethanabooks.modelo;

public class DatosEnvio {

    private String nombre;
    private String direccion;
    private String ciudad;
    private String telefono;

    public DatosEnvio() {}

    public DatosEnvio(String nombre, String direccion, String ciudad, String telefono) {
        this.nombre    = nombre;
        this.direccion = direccion;
        this.ciudad    = ciudad;
        this.telefono  = telefono;
    }

    public boolean estaCompleto() {
        return nombre    != null && !nombre.isBlank()
                && direccion != null && !direccion.isBlank()
                && ciudad    != null && !ciudad.isBlank()
                && telefono  != null && !telefono.isBlank();
    }

    public String getNombre()    { return nombre; }
    public String getDireccion() { return direccion; }
    public String getCiudad()    { return ciudad; }
    public String getTelefono()  { return telefono; }

    public void setNombre(String nombre)       { this.nombre    = nombre; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public void setCiudad(String ciudad)       { this.ciudad    = ciudad; }
    public void setTelefono(String telefono)   { this.telefono  = telefono; }
}