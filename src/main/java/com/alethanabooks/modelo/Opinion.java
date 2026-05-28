package com.alethanabooks.modelo;

import java.time.LocalDateTime;

public class Opinion {
    private String id;
    private String nombreUsuario;
    private String texto;
    private int estrellas;          // 1 a 5
    private LocalDateTime fecha;

    public Opinion() {}

    public Opinion(String id, String nombreUsuario, String texto, int estrellas) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.texto = texto;
        this.estrellas = Math.max(1, Math.min(5, estrellas));
        this.fecha = LocalDateTime.now();
    }

    public String getId()              { return id; }
    public String getNombreUsuario()   { return nombreUsuario; }
    public String getTexto()           { return texto; }
    public int getEstrellas()          { return estrellas; }
    public LocalDateTime getFecha()    { return fecha; }
}