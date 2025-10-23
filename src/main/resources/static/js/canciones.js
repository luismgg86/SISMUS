// canciones.js - manejo de canciones y menú contextual
$(document).ready(function () {
    let selectedSongId = null;

    // Menú contextual (click derecho en canción)
    $(document).on("contextmenu", ".song-card", function (e) {
        e.preventDefault();
        selectedSongId = $(this).data("id");

        $("#contextMenu")
            .css({ top: e.pageY + "px", left: e.pageX + "px" })
            .fadeIn(200);
    });

    $(document).click(function () {
        $("#contextMenu").fadeOut(200);
    });

    // Abrir modal de selección de playlist
    $("#addToPlaylistOption").click(function (e) {
        e.preventDefault();
        $("#selectedSongId").val(selectedSongId);
        $("#selectPlaylistModal").modal("show");
        $("#contextMenu").hide();
    });

    // Agregar canción a playlist (AJAX)
    $("#addToPlaylistForm").submit(function (e) {
        e.preventDefault();

        const playlistId = $("#playlistSelect").val();
        const songId = $("#selectedSongId").val();

        if (!playlistId) {
            alert("Por favor selecciona una playlist.");
            return;
        }

        $.ajax({
            url: '/api/playlists/' + playlistId + '/agregar-cancion',
            type: 'POST',
            data: { cancionId: songId },
            success: function () {
                $("#selectPlaylistModal").modal("hide");
                $("#successAlert").fadeIn().delay(2000).fadeOut();
            },
            error: function (xhr) {
                $("#selectPlaylistModal").modal("hide");
                if (xhr.status === 409) {
                    $("#errorAlert").fadeIn().delay(2000).fadeOut();
                } else {
                    alert("Hubo un error al agregar la canción.");
                }
            }
        });
    });

    // Búsqueda dinámica
    function cargarCanciones(query, page) {
        $.ajax({
            url: '/canciones/buscar',
            type: 'GET',
            data: { q: query, page: page },
            success: function (response) {
                $('#contenedor-canciones').html($(response).find('#contenedor-canciones').html());
            },
            error: function () {
                console.error("Error al cargar canciones");
            }
        });
    }

    $('#buscador').on('input', function () {
        const query = $(this).val();
        cargarCanciones(query, 0);
    });

    $(document).on('click', '.btn-pagina', function (e) {
        e.preventDefault();
        const query = $('#buscador').val();
        const page = $(this).data('page');
        cargarCanciones(query, page);
    });

    // Descargar canción
    $("#downloadSong").click(function (e) {
        e.preventDefault();
        if (!selectedSongId) return;

        window.location.href = "/canciones/" + selectedSongId + "/descargar";

        $("#contextMenu").hide();
    });
});
