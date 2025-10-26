package mx.dgtic.unam.sismus.service;

import mx.dgtic.unam.sismus.dto.ArtistaDetalleDto;
import mx.dgtic.unam.sismus.dto.ArtistaDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ArtistaService {
    List<ArtistaDto> listarTodos();
    List<ArtistaDto> listarActivos();
    List<ArtistaDto> listarInactivos();
    Optional<ArtistaDto> buscarPorId(Integer id);
    ArtistaDto guardar(ArtistaDto artistaDto);
    void actualizar(Integer id, ArtistaDto dto);
    void eliminar(Integer id);
    void activar(Integer id);
    void reactivar(Integer id);
    Optional<ArtistaDto> buscarPorClave(String clave);
    List<ArtistaDto> buscarPorNombre(String nombre);
    Page<ArtistaDto> buscarPorNombrePaginado(String nombre, Pageable pageable);
    Optional<ArtistaDetalleDto> detalleConCancionesPorClave(String clave);
    List<ArtistaDetalleDto> listarConCanciones();
    List<ArtistaDto> artistasSinCanciones();
}
