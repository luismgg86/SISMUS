// detallePlaylist.js — versión compatible con Thymeleaf fragmentos
document.addEventListener('DOMContentLoaded', () => {
    const meta = document.querySelector('meta[name="playlist-id"]');
    if (!meta) return; // seguridad
    const playlistId = meta.content;

    registrarEventosContextuales(playlistId);

    // Agregar canción con AJAX
    const btnAgregar = document.getElementById('btnAgregarCancion');
    if (btnAgregar) {
        btnAgregar.addEventListener('click', e => {
            e.preventDefault();
            const cancionId = document.getElementById('selectCancion').value;
            if (!cancionId) return mostrarAlerta('error', 'Selecciona una canción.');

            fetch(`/api/playlists/${playlistId}/agregar-cancion?cancionId=${cancionId}`, {
                method: 'POST'
            })
                .then(res => {
                    if (res.status === 409) throw new Error('La canción ya está en la playlist.');
                    if (!res.ok) throw new Error('Error al agregar la canción.');
                    return res.text();
                })
                .then(msg => {
                    mostrarAlerta('exito', msg);
                    recargarTablaCanciones(playlistId);
                })
                .catch(err => mostrarAlerta('error', err.message));
        });
    }
});

function mostrarAlerta(tipo, mensaje) {
    const alertaExito = document.getElementById('alertaExito');
    const alertaError = document.getElementById('alertaError');
    const mensajeError = document.getElementById('mensajeError');

    alertaExito.style.display = 'none';
    alertaError.style.display = 'none';

    if (tipo === 'exito') {
        alertaExito.textContent = mensaje;
        alertaExito.style.display = 'block';
        setTimeout(() => alertaExito.style.display = 'none', 2000);
    } else {
        mensajeError.textContent = mensaje;
        alertaError.style.display = 'block';
        setTimeout(() => alertaError.style.display = 'none', 3000);
    }
}

function recargarTablaCanciones(playlistId) {
    fetch(`/playlists/${playlistId}/fragmento`)
        .then(res => res.text())
        .then(html => {
            document.querySelector('#tbodyCanciones').innerHTML = html;
            registrarEventosContextuales(playlistId);
            const totalSpan = document.getElementById('totalCanciones');
            totalSpan.textContent = document.querySelectorAll('.cancion-row').length;
        })
        .catch(err => console.error('Error al recargar canciones:', err));
}

function registrarEventosContextuales(playlistId) {
    const contextMenu = document.getElementById('contextMenu');
    const btnEliminarCancion = document.getElementById('btnEliminarCancion');
    let cancionSeleccionada = null;

    document.querySelectorAll('.cancion-row').forEach(row => {
        row.addEventListener('contextmenu', e => {
            e.preventDefault();
            e.stopPropagation();

            cancionSeleccionada = row.dataset.id;
            contextMenu.style.top = e.pageY + 'px';
            contextMenu.style.left = e.pageX + 'px';
            contextMenu.style.display = 'block';
        });
    });

    document.addEventListener('click', () => contextMenu.style.display = 'none');

    if (btnEliminarCancion) {
        btnEliminarCancion.onclick = () => {
            if (!cancionSeleccionada) return;

            fetch(`/api/playlists/${playlistId}/eliminar-cancion?cancionId=${cancionSeleccionada}`, {
                method: 'DELETE'
            })
                .then(res => {
                    if (!res.ok) throw new Error('Error al eliminar la canción.');
                    return res.text();
                })
                .then(msg => {
                    mostrarAlerta('exito', msg);
                    recargarTablaCanciones(playlistId);
                })
                .catch(err => mostrarAlerta('error', err.message));
        };
    }
}
