package mx.dgtic.unam.sismus.service;

import mx.dgtic.unam.sismus.model.Artista;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ArtistaService {

    // CRUD básico
    List<Artista> listarTodos();
    Optional<Artista> buscarPorId(Integer id);
    Artista guardar(Artista artista);
    void eliminar(Integer id);

    // Búsquedas por atributos
    Optional<Artista> buscarPorClave(String clave);
    List<Artista> buscarPorNombre(String nombre);

    // Paginación
    Page<Artista> buscarPorNombrePaginado(String nombre, Pageable pageable);

    // Relaciones
    Optional<Artista> detalleConCancionesPorClave(String clave);
    List<Artista> listarConCanciones();

    // Reportes
    List<Artista> artistasSinCanciones();
    List<Object[]> reporteArtistasPorTotalCanciones();
    List<Object[]> reporteArtistasMasDescargados();

    // Búsquedas avanzadas
    List<Artista> buscarPorTituloDeCancion(String titulo);
    List<Artista> buscarPorGenero(String genero);
}
