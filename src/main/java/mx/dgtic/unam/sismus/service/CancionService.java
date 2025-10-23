package mx.dgtic.unam.sismus.service;

import mx.dgtic.unam.sismus.dto.CancionRequestDto;
import mx.dgtic.unam.sismus.dto.CancionResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface CancionService {

    // 🔹 Listar canciones activas
    List<CancionResponseDto> listarTodas();

    // 🔹 Listar canciones inactivas
    List<CancionResponseDto> listarInactivas();

    // 🔹 Buscar una canción por ID
    Optional<CancionResponseDto> buscarPorId(Integer id);

    // 🔹 Guardar una canción con archivo
    void guardarCancionConArchivo(CancionRequestDto dto, MultipartFile archivo) throws IOException;

    // 🔹 Actualizar una canción existente
    void actualizarCancion(Integer id, CancionRequestDto dto, MultipartFile archivo) throws IOException;

    // 🔹 Eliminar (desactivar) una canción
    void eliminar(Integer id);

    // 🔹 Reactivar una canción
    void reactivarCancion(Integer id);

    // 🔹 Buscar canciones activas por título (con artista activo)
    Page<CancionResponseDto> buscarPorTituloActivoPaginado(String titulo, Pageable pageable);

    // 🔹 Buscar canciones activas por título, artista o género
    Page<CancionResponseDto> buscarPorTituloArtistaGeneroActivoPaginado(String query, Pageable pageable);
}
