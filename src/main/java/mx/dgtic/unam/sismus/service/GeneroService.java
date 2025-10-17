package mx.dgtic.unam.sismus.service;

import mx.dgtic.unam.sismus.model.Genero;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface GeneroService {

    // CRUD
    List<Genero> listarTodos();
    Optional<Genero> buscarPorId(Integer id);
    Genero guardar(Genero genero);
    void eliminar(Integer id);

    // Búsquedas
    Optional<Genero> buscarPorClave(String clave);
    List<Genero> buscarPorNombre(String nombre);

    // Paginación
    Page<Genero> buscarPorNombrePaginado(String nombre, Pageable pageable);

    // Relaciones
    Optional<Genero> detalleConCancionesPorClave(String clave);

    // Reportes
    List<Genero> generosSinCanciones();
    List<Object[]> reporteConteoCancionesPorGenero();

    // Avanzadas
    List<Genero> buscarPorTituloDeCancion(String titulo);
}
