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

    List<CancionResponseDto> listarTodas();
    Optional<CancionResponseDto> buscarPorId(Integer id);
    CancionResponseDto guardar(CancionRequestDto dto);
    void guardarCancionConArchivo(CancionRequestDto dto, MultipartFile archivo) throws IOException;
    void eliminar(Integer id);
    Page<CancionResponseDto> buscarPorTituloPaginado(String titulo, Pageable pageable);
}
