// canciones.js — buscador, menú contextual, descargas y agregar canciones a playlists
$(function () {

    let selectedSongId = null;

    // --- click derecho en una canción: muestra menú contextual ---
    $(document).on("contextmenu", ".song-card", function (e) {
        e.preventDefault();
        selectedSongId = $(this).data("id");
        $("#contextMenu").css({ top: e.pageY, left: e.pageX }).fadeIn(200);
    });

    // --- clic fuera del menú: lo oculta ---
    $(document).click(() => $("#contextMenu").fadeOut(200));

    // --- abrir modal para agregar canción a playlist ---
    $("#addToPlaylistOption").click(e => {
        e.preventDefault();
        $("#selectedSongId").val(selectedSongId);
        $("#selectPlaylistModal").modal("show");
        $("#contextMenu").hide();
    });

    // --- agregar canción a playlist ---
    $("#addToPlaylistForm").submit(function (e) {
        e.preventDefault();

        const playlistId = $("#playlistSelect").val();
        const songId = $("#selectedSongId").val();

        if (!playlistId) {
            showAlert("Selecciona una playlist.", "warning");
            return;
        }

        $.ajax({
            url: `/api/playlists/${playlistId}/agregar-cancion`,
            type: "POST",
            data: { cancionId: songId },
            success: (res) => {
                $("#selectPlaylistModal").modal("hide");
                const msg = res?.mensaje || "Canción agregada correctamente.";
                showAlert(msg, "success");
            },
            error: (xhr) => {
                $("#selectPlaylistModal").modal("hide");

                let msg = "Ocurrió un error al agregar la canción.";

                // leer el mensaje real del backend
                if (xhr.responseJSON) {
                    msg = xhr.responseJSON.message || xhr.responseJSON.mensaje || msg;
                }

                if (xhr.status === 409) {
                    showAlert(msg, "warning");
                } else if (xhr.status === 404) {
                    showAlert(msg, "danger");
                } else {
                    showAlert(msg, "danger");
                }
            }
        });
    });

    // --- búsqueda dinámica de canciones ---
    function cargarCanciones(query, page) {
        $.ajax({
            url: "/canciones/buscar",
            type: "GET",
            data: { q: query, page },
            success: (response) => {
                $("#contenedor-canciones").html($(response).find("#contenedor-canciones").html());
            },
            error: () => console.error("error al cargar canciones")
        });
    }

    // --- actualiza lista cuando se escribe en el buscador ---
    $("#buscador").on("input", function () {
        cargarCanciones($(this).val(), 0);
    });

    // --- paginación ---
    $(document).on("click", ".btn-pagina", function (e) {
        e.preventDefault();
        const query = $("#buscador").val();
        const page = $(this).data("page");
        cargarCanciones(query, page);
    });

    // --- descargar canción directamente ---
    $("#downloadSong").click(e => {
        e.preventDefault();
        if (!selectedSongId) return;
        window.location.href = `/canciones/${selectedSongId}/descargar`;
        $("#contextMenu").hide();
    });

    // --- función para mostrar alertas flotantes ---
    window.showAlert = function (message, type) {
        const alert = $(`
            <div class="alert alert-${type} alert-fixed shadow">
                <i class="fa-solid ${
                    type === "success"
                        ? "fa-circle-check"
                        : type === "warning"
                        ? "fa-triangle-exclamation"
                        : "fa-circle-xmark"
                } me-2"></i>${message}
            </div>
        `);

        $("body").append(alert);
        alert.fadeIn(200).delay(2500).fadeOut(400, () => alert.remove());
    };
});
