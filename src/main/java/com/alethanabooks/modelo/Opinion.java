package com.alethanabooks.modelo;

import java.time.LocalDateTime;

public class Opinion {
    private String id;
    private String idUsuario;
    private String nombreUsuario;
    private String texto;
    private int estrellas;
    private LocalDateTime fecha;

    public Opinion() {}

    public Opinion(String id, String idUsuario, String nombreUsuario, String texto,
                   int estrellas, LocalDateTime fecha) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.texto = texto;
        this.estrellas = Math.max(1, Math.min(5, estrellas));
        this.fecha = fecha != null ? fecha : LocalDateTime.now();
    }

    public String getId()              { return id; }
    public String getIdUsuario()       { return idUsuario; }
    public String getNombreUsuario()   { return nombreUsuario; }
    public String getTexto()           { return texto; }
    public int getEstrellas()          { return estrellas; }
    public LocalDateTime getFecha()    { return fecha; }
}
