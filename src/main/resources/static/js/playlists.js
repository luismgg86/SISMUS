// playlists.js - menú contextual y eliminación de playlists
$(function () {
    const $menu = $("#playlistContextMenu");
    const $nameHolder = $("#playlistNameToDelete");
    const deleteModal = new bootstrap.Modal(document.getElementById("confirmDeleteModal"));
    let selectedId = null;
    let selectedName = null;

    // Click derecho sobre una playlist
    $(document).on("contextmenu", ".playlist-card", function (e) {
        e.preventDefault();
        selectedId = $(this).data("playlist-id");
        selectedName = $(this).data("playlist-name");

        const x = e.clientX;
        const y = e.clientY;
        $menu.css({ left: x + "px", top: y + "px" }).fadeIn(150);
    });

    // Ocultar menú al hacer clic fuera
    $(document).on("click scroll resize", function () {
        $menu.fadeOut(100);
    });

    // Eliminar playlist
    $("#deletePlaylistAction").on("click", function (e) {
        e.preventDefault();
        $menu.hide();
        $nameHolder.text(selectedName);
        deleteModal.show();
    });

    $("#confirmDeleteBtn").on("click", function () {
        $.ajax({
            url: "/api/playlists/" + selectedId + "/eliminar",
            type: "DELETE",
            headers: { "X-Requested-With": "XMLHttpRequest" },
            success: function () {
                deleteModal.hide();
                $(".playlist-card[data-playlist-id='" + selectedId + "']")
                    .closest(".col-6, .col-md-3").remove();
            },
            error: function () {
                deleteModal.hide();
                alert("Error al eliminar la playlist");
            }
        });
    });
});
