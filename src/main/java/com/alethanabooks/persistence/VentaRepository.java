package com.alethanabooks.persistence;

import com.alethanabooks.modelo.Venta;

import java.nio.file.Path;
import java.util.List;

public class VentaRepository {

    private final JsonManager jsonManager = JsonManager.getInstancia();
    private final Path ruta = Path.of(RutasDatos.VENTAS);

    public List<Venta> obtenerTodas() {
        return jsonManager.leerLista(ruta, Venta.class);
    }

    public void guardar(Venta venta) {
        List<Venta> lista = obtenerTodas();
        lista.add(venta);
        jsonManager.guardarLista(ruta, lista);
    }
}