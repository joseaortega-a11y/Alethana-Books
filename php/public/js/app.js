// Pequeñas interacciones de la interfaz (sin librerías).
document.addEventListener('DOMContentLoaded', () => {

    // Abrir diálogos (Centro de ayuda)
    document.querySelectorAll('[data-abrir]').forEach(boton => {
        boton.addEventListener('click', () => document.getElementById(boton.dataset.abrir)?.showModal());
    });

    // Confirmación antes de eliminar (panel admin)
    document.querySelectorAll('form[data-confirmar]').forEach(form => {
        form.addEventListener('submit', evento => {
            if (!window.confirm(form.dataset.confirmar)) evento.preventDefault();
        });
    });

    // Admin: mostrar Origen para libros físicos y Formato/Archivo para digitales
    const campoTipo = document.getElementById('campo-tipo');
    if (campoTipo) {
        const actualizar = () => {
            document.querySelectorAll('[data-solo]').forEach(el => {
                el.hidden = el.dataset.solo !== campoTipo.value;
            });
        };
        campoTipo.addEventListener('change', actualizar);
        actualizar();
    }

    // Pasarela de pago: simula "Procesando pago..." durante 2 segundos
    const formPago = document.getElementById('form-pago');
    if (formPago) {
        let enviando = false;
        formPago.addEventListener('submit', evento => {
            if (evento.submitter && evento.submitter.value === 'cancelar') return;
            if (enviando) return;
            evento.preventDefault();
            enviando = true;
            const estado = document.getElementById('estado-pago');
            estado.textContent = 'Procesando pago...';
            estado.className = 'estado-pago estado-pago--procesando';
            document.getElementById('barra-progreso').hidden = false;
            document.getElementById('btn-pagar').disabled = true;
            setTimeout(() => formPago.submit(), 2000);
        });
    }
});
