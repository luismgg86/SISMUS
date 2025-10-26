// main.js — scripts globales del layout y manejo del buscador
$(function () {
    const token = $("meta[name='_csrf']").attr("content");
    const header = $("meta[name='_csrf_header']").attr("content");

    // Agrega el token CSRF a todas las peticiones AJAX
    $(document).ajaxSend((e, xhr) => xhr.setRequestHeader(header, token));


    // --- Mostrar buscador automáticamente si venimos de otra vista ---
    if (window.location.pathname.includes("/canciones/buscar") &&
        sessionStorage.getItem("abrirBuscador") === "true") {
        sessionStorage.removeItem("abrirBuscador");
        $(window).on("load", function () {
            const buscador = $("#buscador-container");
            if (buscador.length) {
                buscador.slideDown(300, () => $("#buscador").focus());
            }
        });
    }
});

// Mostrar/ocultar sidebar en pantallas pequeñas
function toggleMenu() {
    const sidebar = document.getElementById('sidebar');
    const isOpen = sidebar.classList.toggle('show');
    document.body.classList.toggle('no-scroll', isOpen);
}
