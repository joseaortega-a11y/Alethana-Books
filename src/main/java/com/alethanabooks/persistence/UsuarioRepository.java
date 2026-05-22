package com.alethanabooks.persistence;

import com.alethanabooks.modelo.Usuario;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class UsuarioRepository {

    private final JsonManager jsonManager = JsonManager.getInstancia();
    private final Path ruta = Path.of(RutasDatos.USUARIOS);

    public List<Usuario> obtenerTodos() {
        return jsonManager.leerLista(ruta, Usuario.class);
    }

    public void guardarTodos(List<Usuario> usuarios) {
        jsonManager.guardarLista(ruta, usuarios);
    }

    public Optional<Usuario> buscarPorCorreo(String correo) {
        return obtenerTodos().stream()
                .filter(u -> u.getCorreo().equalsIgnoreCase(correo))
                .findFirst();
    }

    public void agregar(Usuario usuario) {
        List<Usuario> lista = obtenerTodos();
        lista.add(usuario);
        guardarTodos(lista);
    }
}