package com.alethanabooks.modelo;

public class Usuario {
    private String id;
    private String nombre;
    private String correo;
    private String contrasena;
    private Rol rol;

    public Usuario(String id, String nombre, String correo, String contrasena, Rol rol) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    public boolean credencialesCoinciden(String correo, String contrasena) {
        return this.correo.equalsIgnoreCase(correo) && this.contrasena.equals(contrasena);
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
    public Rol getRol() { return rol; }
}