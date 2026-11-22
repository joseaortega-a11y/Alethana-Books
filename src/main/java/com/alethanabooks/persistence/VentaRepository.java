package com.alethanabooks.persistence;

import com.alethanabooks.modelo.*;

import java.sql.*;
import java.util.*;

public class VentaRepository {

    public List<Venta> obtenerTodas() {
        return consultar(null);
    }

    public List<Venta> obtenerPorUsuario(String idUsuario) {
        return consultar(Integer.parseInt(idUsuario));
    }

    private List<Venta> consultar(Integer idUsuario) {
        String filtro = idUsuario != null ? " WHERE v.id_usuario = ?" : "";
        String sqlVentas = """
                SELECT v.*, u.correo, u.apodo, u.rol
                FROM venta v JOIN usuario u ON u.id_usuario = v.id_usuario
                """ + filtro + " ORDER BY v.fecha, v.id_venta";
        String sqlDetalles = """
                SELECT d.id_venta, d.cantidad, d.precio_unitario, l.id_libro, l.titulo, l.autor
                FROM detalle_venta d
                JOIN libro l ON l.id_libro = d.id_libro
                JOIN venta v ON v.id_venta = d.id_venta
                """ + filtro;

        Map<Integer, Venta> ventas = new LinkedHashMap<>();
        try (Connection con = ConexionBD.obtener()) {
            try (PreparedStatement ps = con.prepareStatement(sqlVentas)) {
                if (idUsuario != null) ps.setInt(1, idUsuario);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) ventas.put(rs.getInt("id_venta"), mapearVenta(rs));
                }
            }
            try (PreparedStatement ps = con.prepareStatement(sqlDetalles)) {
                if (idUsuario != null) ps.setInt(1, idUsuario);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Venta v = ventas.get(rs.getInt("id_venta"));
                        if (v == null) continue;
                        Libro libro = new Libro(String.valueOf(rs.getInt("id_libro")),
                                rs.getString("titulo"), rs.getString("autor"), "",
                                rs.getDouble("precio_unitario"), 0, null);
                        v.getDetalles().add(new DetalleVenta(libro, rs.getInt("cantidad")));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("No se pudieron leer las ventas.", e);
        }
        return new ArrayList<>(ventas.values());
    }

    /**
     * Guarda la venta y sus detalles en una transacción.
     * Los triggers de detalle_venta validan y descuentan el stock automáticamente.
     */
    public void guardar(Venta venta) {
        String sqlVenta = """
                INSERT INTO venta (id_usuario, metodo_pago, tipo_descuento, codigo, subtotal, descuento, total,
                                   envio_nombre, envio_direccion, envio_ciudad, envio_telefono)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        String sqlDetalle = """
                INSERT INTO detalle_venta (id_venta, id_libro, cantidad, precio_unitario, subtotal_linea)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection con = ConexionBD.obtener()) {
            con.setAutoCommit(false);
            try {
                int idVenta;
                DatosEnvio envio = venta.getDatosEnvio();
                try (PreparedStatement ps = con.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, Integer.parseInt(venta.getUsuario().getId()));
                    ps.setString(2, venta.getMetodoPago().toUpperCase());
                    ps.setString(3, venta.getTipoDescuento());
                    ps.setString(4, venta.getCodigo());
                    ps.setDouble(5, venta.getSubtotal());
                    ps.setDouble(6, venta.getDescuento());
                    ps.setDouble(7, venta.getTotal());
                    ps.setString(8, envio.getNombre());
                    ps.setString(9, envio.getDireccion());
                    ps.setString(10, envio.getCiudad());
                    ps.setString(11, envio.getTelefono());
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.next();
                        idVenta = keys.getInt(1);
                    }
                }
                try (PreparedStatement ps = con.prepareStatement(sqlDetalle)) {
                    for (DetalleVenta d : venta.getDetalles()) {
                        ps.setInt(1, idVenta);
                        ps.setInt(2, Integer.parseInt(d.getLibro().getId()));
                        ps.setInt(3, d.getCantidad());
                        ps.setDouble(4, d.getLibro().getPrecio());
                        ps.setDouble(5, d.getSubtotal());
                        ps.executeUpdate();
                    }
                }
                con.commit();
                venta.setId(String.valueOf(idVenta));
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            // Los triggers lanzan mensajes como "Stock insuficiente..." que conviene mostrar
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private Venta mapearVenta(ResultSet rs) throws SQLException {
        Usuario u = new Usuario(String.valueOf(rs.getInt("id_usuario")), rs.getString("apodo"),
                rs.getString("correo"), null,
                "ADMIN".equals(rs.getString("rol")) ? Rol.ADMIN : Rol.USUARIO);
        DatosEnvio envio = new DatosEnvio(rs.getString("envio_nombre"), rs.getString("envio_direccion"),
                rs.getString("envio_ciudad"), rs.getString("envio_telefono"));

        Venta v = new Venta(String.valueOf(rs.getInt("id_venta")), u, new ArrayList<>(),
                metodoDesdeBD(rs.getString("metodo_pago")), rs.getDouble("total"), "PAGADO", envio);
        v.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
        v.setSubtotal(rs.getDouble("subtotal"));
        v.setDescuento(rs.getDouble("descuento"));
        v.setTipoDescuento(rs.getString("tipo_descuento"));
        v.setCodigo(rs.getString("codigo"));
        return v;
    }

    // En la BD: TARJETA / PSE / EFECTY; en la app: Tarjeta / PSE / Efecty
    private static String metodoDesdeBD(String metodo) {
        if (metodo == null) return "";
        return switch (metodo) {
            case "TARJETA" -> "Tarjeta";
            case "EFECTY"  -> "Efecty";
            default        -> metodo;
        };
    }
}
