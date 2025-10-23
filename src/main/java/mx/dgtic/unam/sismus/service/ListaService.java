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

    void agregarCancionALista(Integer listaId, Integer cancionId);

    void eliminarCancionDeLista(Integer listaId, Integer cancionId);

    void eliminar(Integer id);

    /**
     * 🔹 Nueva función que descarga la playlist como ZIP
     * y registra las descargas del usuario autenticado (si aplica)
     */
    void descargarPlaylistComoZip(Integer playlistId, String nickname, HttpServletResponse response) throws IOException;
}
