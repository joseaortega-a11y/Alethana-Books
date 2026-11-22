package com.alethanabooks.persistence;

import java.sql.*;
import java.util.Optional;

public class CodigoDescuentoRepository {

    /** Devuelve el porcentaje (ej. 20) del código, si existe. */
    public Optional<Integer> buscarPorcentaje(String codigo) {
        String sql = "SELECT porcentaje FROM codigo_descuento WHERE codigo = ?";
        try (Connection con = ConexionBD.obtener();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(rs.getInt("porcentaje")) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo consultar el código de descuento.", e);
        }
    }
}
