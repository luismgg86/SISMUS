package mx.dgtic.unam.sismus.service;

import mx.dgtic.unam.sismus.dto.ArtistaDetalleDto;
import mx.dgtic.unam.sismus.dto.ArtistaDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ArtistaService {

    // CRUD básico
    List<ArtistaDto> listarTodos();
    Optional<ArtistaDto> buscarPorId(Integer id);
    ArtistaDto guardar(ArtistaDto artistaDto);
    void eliminar(Integer id);

    // Búsquedas
    Optional<ArtistaDto> buscarPorClave(String clave);
    List<ArtistaDto> buscarPorNombre(String nombre);
    Page<ArtistaDto> buscarPorNombrePaginado(String nombre, Pageable pageable);

    // Relaciones
    Optional<ArtistaDetalleDto> detalleConCancionesPorClave(String clave);
    List<ArtistaDetalleDto> listarConCanciones();

    // Reportes
    List<ArtistaDto> artistasSinCanciones();
}
