package mx.dgtic.unam.sismus.service;

import mx.dgtic.unam.sismus.model.Lista;

import java.util.List;
import java.util.Optional;

public interface ListaService {

    List<Lista> listarTodas();
    Optional<Lista> buscarPorId(Integer id);
    Lista guardar(Lista lista);
    void eliminar(Integer id);

    // Para funcionalidad
    Lista obtenerConRelaciones(Integer id);

    void agregarCancionALista(Integer listaId, Integer cancionId);

    void eliminarCancionDeLista(Integer listaId, Integer cancionId);

    List<Lista> obtenerListasPorUsuario(String nickname);

    public Lista crearPlaylist(String nombre);
}
