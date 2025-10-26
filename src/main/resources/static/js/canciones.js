// canciones.js — buscador, menu contextual, descargas y agregar canciones a playlists
$(function () {

    let selectedSongId = null;

    // Click derecho en una cancion -> muestra menu contextual
    $(document).on("contextmenu", ".song-card", function (e) {
        e.preventDefault();
        selectedSongId = $(this).data("id");
        $("#contextMenu").css({ top: e.pageY, left: e.pageX }).fadeIn(200);
    });

    // Clic fuera -> se cierra el menu
    $(document).click(() => $("#contextMenu").fadeOut(200));

    // Abre modal para agregar canciona la playlist
    $("#addToPlaylistOption").click(e => {
        e.preventDefault();
        $("#selectedSongId").val(selectedSongId);
        $("#selectPlaylistModal").modal("show");
        $("#contextMenu").hide();
    });

    // Para agregar canciona playlist
    $("#addToPlaylistForm").submit(function (e) {
        e.preventDefault();

        const playlistId = $("#playlistSelect").val();
        const songId = $("#selectedSongId").val();

        if (!playlistId) return showAlert("Selecciona una playlist.", "warning");

        $.ajax({
            url: `/api/playlists/${playlistId}/agregar-cancion`,
            type: "POST",
            data: { cancionId: songId },
            success: () => {
                $("#selectPlaylistModal").modal("hide");
                showAlert("Canción agregada correctamente.", "success");
            },
            error: xhr => {
                $("#selectPlaylistModal").modal("hide");
                if (xhr.status === 409)
                    showAlert("La canción ya está en la playlist.", "warning");
                else
                    showAlert("Ocurrió un error al agregar la canción.", "danger");
            }
        });
    });

    // Busqueda dinamica de canciones
    function cargarCanciones(query, page) {
        $.ajax({
            url: "/canciones/buscar",
            type: "GET",
            data: { q: query, page },
            success: response => {
                $("#contenedor-canciones").html($(response).find("#contenedor-canciones").html());
            },
            error: () => console.error("Error al cargar canciones")
        });
    }

    // Cada vez que el usuario escribe, se actualiza la lista
    $("#buscador").on("input", function () {
        cargarCanciones($(this).val(), 0);
    });

    // Paginacion
    $(document).on("click", ".btn-pagina", function (e) {
        e.preventDefault();
        const query = $("#buscador").val();
        const page = $(this).data("page");
        cargarCanciones(query, page);
    });

    // Descargar cancion directamente
    $("#downloadSong").click(e => {
        e.preventDefault();
        if (!selectedSongId) return;
        window.location.href = `/canciones/${selectedSongId}/descargar`;
        $("#contextMenu").hide();
    });
});
