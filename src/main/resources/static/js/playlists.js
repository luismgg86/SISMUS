// playlists.js - menú contextual, creación y eliminación de playlists
$(function () {

    const $menu = $("#playlistContextMenu");
    const $nameHolder = $("#playlistNameToDelete");
    const deleteModal = new bootstrap.Modal(document.getElementById("confirmDeleteModal"));
    const createModal = new bootstrap.Modal(document.getElementById("crearPlaylistModal"));
    let selectedId = null;
    let selectedName = null;

    // CSRF
    const token = $("meta[name='_csrf']").attr("content");
    const header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function (e, xhr) {
        xhr.setRequestHeader(header, token);
    });

    // Click derecho sobre una playlist
    $(document).on("contextmenu", ".playlist-card", function (e) {
        e.preventDefault();
        selectedId = $(this).data("playlist-id");
        selectedName = $(this).data("playlist-name");
        $menu.css({ left: e.clientX + "px", top: e.clientY + "px" }).fadeIn(150);
    });

    // Ocultar menú contextual
    $(document).on("click scroll resize", function () {
        $menu.fadeOut(100);
    });

    // Abrir modal de confirmación de eliminación
    $("#deletePlaylistAction").on("click", function (e) {
        e.preventDefault();
        $menu.hide();
        $nameHolder.text(selectedName);
        deleteModal.show();
    });

    //Confirmar eliminación
    $("#confirmDeleteBtn").on("click", function () {
        $.ajax({
            url: "/api/playlists/" + selectedId + "/eliminar",
            type: "DELETE",
            headers: { "X-Requested-With": "XMLHttpRequest" },
            success: function (response) {
                deleteModal.hide();
                showAlert(response.mensaje, "success");
                $(".playlist-card[data-playlist-id='" + selectedId + "']")
                    .closest(".col-6, .col-md-3")
                    .fadeOut(400, function () { $(this).remove(); });
                setTimeout(() => location.reload(), 1000);
            },
            error: function (xhr) {
                deleteModal.hide();
                const msg = xhr.responseJSON?.error || "Error al eliminar la playlist.";
                showAlert(msg, "danger");
            }
        });
    });

    // ===== Crear nueva playlist (AJAX) =====
    $("#crearPlaylistForm").on("submit", function (e) {
        e.preventDefault();
        const nombre = $("#nombre").val().trim();
        const usuarioId = $("#usuarioId").val() || 1;
        if (!nombre) return;

        $.ajax({
            url: "/api/playlists/crear",
            type: "POST",
            data: { nombre, usuarioId },
            headers: { "X-Requested-With": "XMLHttpRequest" },
            success: function (response) {
                createModal.hide();
                $("#crearPlaylistForm")[0].reset();
                showAlert(response.mensaje, "success");
                setTimeout(() => location.reload(), 1000);
            },
            error: function (xhr) {
                const msg = xhr.responseJSON?.error || "Error al crear la playlist.";
                showAlert(msg, "danger");
            }
        });
    });

    function showAlert(message, type = "info") {
        const $alert = $(`
            <div class="alert alert-${type} alert-dismissible fade show text-center shadow-lg" role="alert">
                ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        `);
        $("#alertContainer").html($alert);
        setTimeout(() => $alert.alert('close'), 4000);
    }
});
