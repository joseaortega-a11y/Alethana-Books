package com.alethanabooks.service;

import com.alethanabooks.modelo.Rol;
import com.alethanabooks.modelo.Usuario;
import com.alethanabooks.persistence.UsuarioRepository;

import java.util.Optional;

public class UsuarioService {

    private final UsuarioRepository usuarioRepository = new UsuarioRepository();

    public Optional<Usuario> autenticar(String correo, String contrasena) {
        return usuarioRepository.buscarPorCorreo(correo)
                .filter(u -> u.credencialesCoinciden(correo, contrasena));
    }

    public boolean esAdmin(String correo, String contrasena) {
        return autenticar(correo, contrasena)
                .map(u -> u.getRol() == Rol.ADMIN)
                .orElse(false);
    }

    /** Registra un cliente nuevo. Si el correo ya existe no hace nada. */
    public void registrar(String nombre, String correo, String contrasena) {
        if (usuarioRepository.buscarPorCorreo(correo).isPresent()) return;
        Usuario nuevo = new Usuario(null, nombre, correo, null, Rol.USUARIO);
        usuarioRepository.agregar(nuevo, Usuario.hashear(contrasena));
    }

    public void inicializarAdminPorDefecto() {
        if (!usuarioRepository.existeAdmin()) {
            Usuario admin = new Usuario(null, "Admin", "admin@alethana.com", null, Rol.ADMIN);
            usuarioRepository.agregar(admin, Usuario.hashear("admin123"));
        }
    }
}
