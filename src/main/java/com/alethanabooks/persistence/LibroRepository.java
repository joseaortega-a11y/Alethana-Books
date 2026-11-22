package com.alethanabooks.persistence;

import com.alethanabooks.modelo.Libro;
import com.alethanabooks.modelo.LibroDigital;
import com.alethanabooks.modelo.LibroFisico;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LibroRepository {

    private static final String SELECT_BASE = """
            SELECT l.id_libro, l.titulo, l.autor, c.nombre AS categoria, l.precio, l.stock,
                   l.ruta_imagen, l.tipo,
                   f.peso, f.bodega, f.origen,
                   d.formato, d.ruta_archivo
            FROM libro l
            JOIN categoria c ON c.id_categoria = l.id_categoria
            LEFT JOIN libro_fisico  f ON f.id_libro = l.id_libro
            LEFT JOIN libro_digital d ON d.id_libro = l.id_libro
            WHERE l.activo = 1
            """;

    public List<Libro> obtenerTodos() {
        List<Libro> libros = new ArrayList<>();
        String sql = SELECT_BASE + " ORDER BY l.fecha_registro, l.id_libro";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) libros.add(mapear(rs));
        } catch (SQLException e) {
            throw new RuntimeException("No se pudieron leer los libros.", e);
        }
        return libros;
    }

    public Optional<Libro> buscarPorId(String id) {
        String sql = SELECT_BASE + " AND l.id_libro = ?";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(id));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException | NumberFormatException e) {
            throw new RuntimeException("No se pudo buscar el libro " + id, e);
        }
    }

    public List<String> obtenerCategorias() {
        List<String> categorias = new ArrayList<>();
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement("SELECT nombre FROM categoria ORDER BY nombre");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) categorias.add(rs.getString("nombre"));
        } catch (SQLException e) {
            throw new RuntimeException("No se pudieron leer las categorías.", e);
        }
        return categorias;
    }

    public void agregar(Libro libro) {
        String tipo = (libro instanceof LibroDigital) ? "DIGITAL" : "FISICO";
        String sqlLibro = """
                INSERT INTO libro (titulo, autor, id_categoria, precio, stock, ruta_imagen, tipo)
                VALUES (?, ?, (SELECT id_categoria FROM categoria WHERE nombre = ?), ?, ?, ?, ?)
                """;
        try (Connection con = ConexionBD.obtener()) {
            con.setAutoCommit(false);
            try {
                int idGenerado;
                try (PreparedStatement ps = con.prepareStatement(sqlLibro, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, libro.getTitulo());
                    ps.setString(2, libro.getAutor());
                    ps.setString(3, libro.getCategoria());
                    ps.setDouble(4, libro.getPrecio());
                    ps.setInt(5, libro.getStock());
                    ps.setString(6, vacioANull(libro.getImagen()));
                    ps.setString(7, tipo);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.next();
                        idGenerado = keys.getInt(1);
                    }
                }

                if (libro instanceof LibroDigital ld) {
                    try (PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO libro_digital (id_libro, formato, ruta_archivo) VALUES (?, ?, ?)")) {
                        ps.setInt(1, idGenerado);
                        ps.setString(2, ld.getFormato());
                        ps.setString(3, ld.getRutaArchivo() != null ? ld.getRutaArchivo() : "");
                        ps.executeUpdate();
                    }
                } else if (libro instanceof LibroFisico lf) {
                    try (PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO libro_fisico (id_libro, peso, bodega, origen) VALUES (?, ?, ?, ?)")) {
                        ps.setInt(1, idGenerado);
                        ps.setDouble(2, lf.getPeso());
                        ps.setString(3, lf.getUbicacionBodega() != null ? lf.getUbicacionBodega() : "Bodega Central");
                        ps.setString(4, origenABD(lf.getOrigen()));
                        ps.executeUpdate();
                    }
                }
                con.commit();
                libro.setId(String.valueOf(idGenerado));
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo agregar el libro.", e);
        }
    }

    public void actualizar(Libro libro) {
        String sqlLibro = """
                UPDATE libro
                   SET titulo = ?, autor = ?,
                       id_categoria = (SELECT id_categoria FROM categoria WHERE nombre = ?),
                       precio = ?, stock = ?, ruta_imagen = ?
                 WHERE id_libro = ?
                """;
        int id = Integer.parseInt(libro.getId());
        try (Connection con = ConexionBD.obtener()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement ps = con.prepareStatement(sqlLibro)) {
                    ps.setString(1, libro.getTitulo());
                    ps.setString(2, libro.getAutor());
                    ps.setString(3, libro.getCategoria());
                    ps.setDouble(4, libro.getPrecio());
                    ps.setInt(5, libro.getStock());
                    ps.setString(6, vacioANull(libro.getImagen()));
                    ps.setInt(7, id);
                    ps.executeUpdate();
                }
                if (libro instanceof LibroDigital ld) {
                    try (PreparedStatement ps = con.prepareStatement(
                            "UPDATE libro_digital SET formato = ?, ruta_archivo = ? WHERE id_libro = ?")) {
                        ps.setString(1, ld.getFormato());
                        ps.setString(2, ld.getRutaArchivo() != null ? ld.getRutaArchivo() : "");
                        ps.setInt(3, id);
                        ps.executeUpdate();
                    }
                } else if (libro instanceof LibroFisico lf) {
                    try (PreparedStatement ps = con.prepareStatement(
                            "UPDATE libro_fisico SET origen = ? WHERE id_libro = ?")) {
                        ps.setString(1, origenABD(lf.getOrigen()));
                        ps.setInt(2, id);
                        ps.executeUpdate();
                    }
                }
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo actualizar el libro.", e);
        }
    }

    /** Borrado lógico: el libro puede tener ventas asociadas, así que solo se desactiva. */
    public void eliminar(String id) {
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement("UPDATE libro SET activo = 0 WHERE id_libro = ?")) {
            ps.setInt(1, Integer.parseInt(id));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo eliminar el libro.", e);
        }
    }

    private Libro mapear(ResultSet rs) throws SQLException {
        String id        = String.valueOf(rs.getInt("id_libro"));
        String titulo    = rs.getString("titulo");
        String autor     = rs.getString("autor");
        String categoria = rs.getString("categoria");
        double precio    = rs.getDouble("precio");
        int    stock     = rs.getInt("stock");
        String imagen    = rs.getString("ruta_imagen");

        if ("DIGITAL".equals(rs.getString("tipo"))) {
            String ruta = rs.getString("ruta_archivo");
            return new LibroDigital(id, titulo, autor, categoria, precio, stock, imagen,
                    ruta, ruta != null && !ruta.isBlank(), rs.getString("formato"));
        }
        return new LibroFisico(id, titulo, autor, categoria, precio, stock, imagen,
                rs.getDouble("peso"), rs.getString("bodega"), origenDesdeBD(rs.getString("origen")));
    }

    // En la BD se guarda NACIONAL / IMPORTADO; la app muestra Nacional / Importado
    private static String origenABD(String origen) {
        return "Importado".equalsIgnoreCase(origen) ? "IMPORTADO" : "NACIONAL";
    }

    private static String origenDesdeBD(String origen) {
        return "IMPORTADO".equalsIgnoreCase(origen) ? "Importado" : "Nacional";
    }

    private static String vacioANull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }
}
