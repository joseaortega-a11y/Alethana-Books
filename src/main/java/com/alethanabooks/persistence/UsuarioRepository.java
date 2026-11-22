package com.alethanabooks.persistence;

import com.alethanabooks.modelo.DatosEnvio;
import com.alethanabooks.modelo.Rol;
import com.alethanabooks.modelo.Usuario;

import java.sql.*;
import java.util.Optional;

public class UsuarioRepository {

    private static final String SELECT_BASE = """
            SELECT u.id_usuario, u.correo, u.apodo, u.contrasena_hash, u.rol,
                   e.nombre_destinatario, e.direccion, e.ciudad, e.telefono
            FROM usuario u
            LEFT JOIN datos_envio e ON e.id_usuario = u.id_usuario
            """;

    public Optional<Usuario> buscarPorCorreo(String correo) {
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(SELECT_BASE + " WHERE u.correo = ?")) {
            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo buscar el usuario.", e);
        }
    }

    public boolean existeAdmin() {
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement("SELECT 1 FROM usuario WHERE rol = 'ADMIN' LIMIT 1");
             ResultSet rs = ps.executeQuery()) {
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo consultar administradores.", e);
        }
    }

    /** Inserta el usuario; el trigger trg_usuario_ai lo agrega a cliente/administrador. */
    public void agregar(Usuario usuario, String contrasenaHash) {
        String sql = "INSERT INTO usuario (correo, apodo, contrasena_hash, rol) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario.getCorreo());
            ps.setString(2, usuario.getNombre());
            ps.setString(3, contrasenaHash);
            ps.setString(4, usuario.getRol() == Rol.ADMIN ? "ADMIN" : "USER");
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo registrar el usuario.", e);
        }
    }

    /** Guarda (o actualiza) los datos de envío del usuario. */
    public void actualizar(Usuario usuario) {
        DatosEnvio envio = usuario.getDatosEnvio();
        if (envio == null) return;
        String sql = """
                INSERT INTO datos_envio (id_usuario, nombre_destinatario, direccion, ciudad, telefono)
                VALUES (?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE nombre_destinatario = VALUES(nombre_destinatario),
                                        direccion = VALUES(direccion),
                                        ciudad    = VALUES(ciudad),
                                        telefono  = VALUES(telefono)
                """;
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(usuario.getId()));
            ps.setString(2, envio.getNombre());
            ps.setString(3, envio.getDireccion());
            ps.setString(4, envio.getCiudad());
            ps.setString(5, envio.getTelefono());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("No se pudieron guardar los datos de envío.", e);
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = new Usuario(
                String.valueOf(rs.getInt("id_usuario")),
                rs.getString("apodo"),
                rs.getString("correo"),
                rs.getString("contrasena_hash"),
                "ADMIN".equals(rs.getString("rol")) ? Rol.ADMIN : Rol.USUARIO);

        if (rs.getString("direccion") != null) {
            u.setDatosEnvio(new DatosEnvio(
                    rs.getString("nombre_destinatario"),
                    rs.getString("direccion"),
                    rs.getString("ciudad"),
                    rs.getString("telefono")));
        }
        return u;
    }
}
