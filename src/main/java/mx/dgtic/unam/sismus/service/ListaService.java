package mx.dgtic.unam.sismus.service;

import mx.dgtic.unam.sismus.dto.ListaRequestDto;
import mx.dgtic.unam.sismus.dto.ListaResponseDto;

import java.util.List;

public interface ListaService {

    ListaResponseDto crearPlaylist(ListaRequestDto dto);
    ListaResponseDto obtenerConRelaciones(Integer id);
    List<ListaResponseDto> obtenerListasPorUsuario(String nickname);
    void agregarCancionALista(Integer listaId, Integer cancionId);
    void eliminarCancionDeLista(Integer listaId, Integer cancionId);
    void eliminar(Integer id);
}
