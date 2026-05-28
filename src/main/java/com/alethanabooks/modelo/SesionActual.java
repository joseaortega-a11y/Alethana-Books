package com.alethanabooks.modelo;

import com.alethanabooks.modelo.Usuario;
import com.alethanabooks.service.CarritoService;

public class SesionActual {

    private static Usuario usuario;
    private static final CarritoService carritoService = new CarritoService();

    private SesionActual() {}

    public static void iniciar(Usuario u) {
        usuario = u;
        carritoService.iniciarCarrito(u);
    }

    public static Usuario getUsuario() { return usuario; }
    public static CarritoService getCarritoService() { return carritoService; }

    public static boolean haySesion() { return usuario != null; }
}