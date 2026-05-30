package com.alethanabooks.service;

import com.alethanabooks.modelo.Rol;
import com.alethanabooks.modelo.Usuario;
import com.alethanabooks.persistence.UsuarioRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UsuarioService {

    private final UsuarioRepository usuarioRepository = new UsuarioRepository();

    public Optional<Usuario> autenticar(String correo, String contrasena) {
        List<Usuario> todos = usuarioRepository.obtenerTodos();
        return todos.stream()
                .filter(u -> u.credencialesCoinciden(correo, contrasena))
                .findFirst();
    }

    public boolean esAdmin(String correo, String contrasena) {
        return autenticar(correo, contrasena)
                .map(u -> u.getRol() == Rol.ADMIN)
                .orElse(false);
    }

    public void registrar(String nombre, String correo, String contrasena) {
        Usuario nuevo = new Usuario(
                UUID.randomUUID().toString(),
                nombre,
                correo,
                contrasena,
                Rol.USUARIO
        );
        usuarioRepository.agregar(nuevo);
    }

    public void inicializarAdminPorDefecto() {
        List<Usuario> todos = usuarioRepository.obtenerTodos();
        boolean hayAdmin = todos.stream().anyMatch(u -> u.getRol() == Rol.ADMIN);
        if (!hayAdmin) {
            registrarAdmin("Admin", "admin@alethana.com", "admin123");
        }
    }

    private void registrarAdmin(String nombre, String correo, String contrasena) {
        Usuario admin = new Usuario(
                UUID.randomUUID().toString(),
                nombre,
                correo,
                contrasena,
                Rol.ADMIN
        );
        usuarioRepository.agregar(admin);
    }
}