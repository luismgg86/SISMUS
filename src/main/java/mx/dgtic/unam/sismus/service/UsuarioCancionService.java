package mx.dgtic.unam.sismus.service;

import mx.dgtic.unam.sismus.model.UsuarioCancion;

import java.util.List;

public interface UsuarioCancionService {

    UsuarioCancion registrarDescarga(UsuarioCancion usuarioCancion);

    UsuarioCancion registrarDescargaPorIds(Integer usuarioId, Integer cancionId);

    //Obtiene todas las descargas de un usuario
    List<UsuarioCancion> obtenerDescargasPorUsuario(Integer usuarioId);

    //Obtiene todas las descargas de una canción.
    List<UsuarioCancion> obtenerDescargasPorCancion(Integer cancionId);

    // Elimina una descarga específica (por ID).
    void eliminarDescarga(Integer id);

    // Verifica si un usuario ya descargó cierta canción.
    boolean existeDescarga(Integer usuarioId, Integer cancionId);
}
