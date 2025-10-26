// detallePlaylist.js — se encarga de manejar las canciones dentro de una playlist
document.addEventListener("DOMContentLoaded", () => {

    const meta = document.querySelector('meta[name="playlist-id"]');
    if (!meta) return; // si no hay meta, salimos
    const playlistId = meta.content;

    registrarEventosContextuales(playlistId);

    // Para el boton de agregar cancion
    const btnAgregar = document.getElementById("btnAgregarCancion");
    if (btnAgregar) {
        btnAgregar.addEventListener("click", e => {
            e.preventDefault();
            const cancionId = document.getElementById("selectCancion").value;
            if (!cancionId) return showAlert("Selecciona una canción.", "warning");

            // Se manda la peticion al controlador
            fetch(`/api/playlists/${playlistId}/agregar-cancion?cancionId=${cancionId}`, { method: "POST" })
                .then(res => {
                    if (res.status === 409) throw new Error("La canción ya está en la playlist.");
                    if (!res.ok) throw new Error("Error al agregar la canción.");
                    return res.text();
                })
                .then(msg => {
                    showAlert(msg, "success");
                    recargarTablaCanciones(playlistId);
                })
                .catch(err => showAlert(err.message, "danger"));
        });
    }
});

// Para recargar la tablas de canciones despues de agregar
function recargarTablaCanciones(playlistId) {
    fetch(`/playlists/${playlistId}/fragmento`)
        .then(res => res.text())
        .then(html => {
            document.querySelector("#tbodyCanciones").innerHTML = html;
            registrarEventosContextuales(playlistId);
            document.getElementById("totalCanciones").textContent =
                document.querySelectorAll(".cancion-row").length;
        })
        .catch(err => console.error("Error al recargar canciones:", err));
}

function registrarEventosContextuales(playlistId) {
    const contextMenu = document.getElementById("contextMenu");
    const btnEliminar = document.getElementById("btnEliminarCancion");
    let cancionSeleccionada = null;

    // Click derecho sobre una fila de cancion
    document.querySelectorAll(".cancion-row").forEach(row => {
        row.addEventListener("contextmenu", e => {
            e.preventDefault();
            cancionSeleccionada = row.dataset.id;
            contextMenu.style.top = e.pageY + "px";
            contextMenu.style.left = e.pageX + "px";
            contextMenu.style.display = "block";
        });
    });

    // Si se hace click fuera se cierra
    document.addEventListener("click", () => contextMenu.style.display = "none");

    // Para eliminar la cancion
    if (btnEliminar) {
        btnEliminar.onclick = () => {
            if (!cancionSeleccionada) return;
            fetch(`/api/playlists/${playlistId}/eliminar-cancion?cancionId=${cancionSeleccionada}`, {
                method: "DELETE"
            })
                .then(res => {
                    if (!res.ok) throw new Error("Error al eliminar la canción.");
                    return res.text();
                })
                .then(msg => {
                    showAlert(msg, "success");
                    recargarTablaCanciones(playlistId);
                })
                .catch(err => showAlert(err.message, "danger"));
        };
    }
}
