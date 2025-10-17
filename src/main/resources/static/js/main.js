//main.js - scripts globales del layout principal
$(document).ready(function() {
    const token = $("meta[name='_csrf']").attr("content");
    const header = $("meta[name='_csrf_header']").attr("content");

    //Añadir token CSRF a todas las peticiones AJAX
    $(document).ajaxSend(function(e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });
});

//Mostrar/ocultar sidebar en pantallas pequeñas
function toggleMenu() {
    document.getElementById('sidebar').classList.toggle('show');
}
