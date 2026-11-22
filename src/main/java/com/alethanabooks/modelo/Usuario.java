package com.alethanabooks.modelo;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class Usuario {
    private String id;
    private String nombre;
    private String correo;
    private String contrasena;   // hash SHA-256 guardado en la BD
    private Rol rol;
    private DatosEnvio datosEnvio;

    public Usuario(String id, String nombre, String correo, String contrasena, Rol rol) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    /** Acepta el hash SHA-256 o, por compatibilidad con datos de prueba, el texto plano. */
    public boolean credencialesCoinciden(String correo, String contrasena) {
        if (this.correo == null || this.contrasena == null || contrasena == null) return false;
        return this.correo.equalsIgnoreCase(correo)
                && (this.contrasena.equalsIgnoreCase(hashear(contrasena))
                    || this.contrasena.equals(contrasena));
    }

    public static String hashear(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(texto.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
    public Rol getRol() { return rol; }
    public DatosEnvio getDatosEnvio()                { return datosEnvio; }
    public void setDatosEnvio(DatosEnvio datosEnvio) { this.datosEnvio = datosEnvio; }
}
