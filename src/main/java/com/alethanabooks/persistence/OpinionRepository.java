package com.alethanabooks.persistence;

import com.alethanabooks.modelo.Opinion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OpinionRepository {

    /** Devuelve las opiniones de la más antigua a la más reciente. */
    public List<Opinion> obtenerTodas() {
        String sql = """
                SELECT o.id_opinion, o.id_usuario, u.apodo, o.texto, o.calificacion, o.fecha_hora
                FROM opinion o JOIN usuario u ON u.id_usuario = o.id_usuario
                ORDER BY o.fecha_hora, o.id_opinion
                """;
        List<Opinion> lista = new ArrayList<>();
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Opinion(
                        String.valueOf(rs.getInt("id_opinion")),
                        String.valueOf(rs.getInt("id_usuario")),
                        rs.getString("apodo"),
                        rs.getString("texto"),
                        rs.getInt("calificacion"),
                        rs.getTimestamp("fecha_hora").toLocalDateTime()));
            }
        } catch (SQLException e) {
            throw new RuntimeException("No se pudieron leer las opiniones.", e);
        }
        return lista;
    }

    public void agregar(Opinion opinion) {
        String sql = "INSERT INTO opinion (id_usuario, texto, calificacion) VALUES (?, ?, ?)";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(opinion.getIdUsuario()));
            ps.setString(2, opinion.getTexto());
            ps.setInt(3, opinion.getEstrellas());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo guardar la opinión.", e);
        }
    }
}
