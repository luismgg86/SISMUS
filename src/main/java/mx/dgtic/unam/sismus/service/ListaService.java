package mx.dgtic.unam.sismus.service;

import jakarta.servlet.http.HttpServletResponse;
import mx.dgtic.unam.sismus.dto.ListaRequestDto;
import mx.dgtic.unam.sismus.dto.ListaResponseDto;

import java.io.IOException;
import java.util.List;

public interface ListaService {
    ListaResponseDto crearPlaylist(ListaRequestDto dto);
    ListaResponseDto obtenerConRelaciones(Integer id);
    List<ListaResponseDto> obtenerListasPorUsuario(String nickname);
    void crearPlaylistVacia(String nombre, Integer usuarioId);
    void agregarCancionALista(Integer listaId, Integer cancionId);
    void eliminarCancionDeLista(Integer listaId, Integer cancionId);
    void eliminar(Integer id);
    void descargarPlaylistComoZip(Integer playlistId, String nickname, HttpServletResponse response) throws IOException;
}
