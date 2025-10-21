package mx.dgtic.unam.sismus.repository;

import mx.dgtic.unam.sismus.model.Genero;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GeneroRepository extends JpaRepository<Genero, Integer> {

    /* ==========================
       Búsquedas básicas
       ========================== */

    // Buscar por clave exacta
    Optional<Genero> findByClave(String clave);

    // Buscar por clave sin importar mayúsculas
    List<Genero> findByClaveIgnoreCase(String clave);

    // Buscar por nombre parcial
    List<Genero> findByNombreContainingIgnoreCase(String nombre);

    // Búsqueda paginada (ideal para catálogos)
    Page<Genero> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    // Ordenar alfabéticamente
    List<Genero> findAllByOrderByNombreAsc();

    boolean existsByClave(String clave);

    /* ==========================
       Optimización para relaciones
       ========================== */

    // Traer género y todas sus canciones
    @EntityGraph(attributePaths = "canciones")
    @Query("SELECT g FROM Genero g WHERE g.clave = :clave")
    Optional<Genero> detalleConCancionesPorClave(@Param("clave") String clave);

    /* ==========================
       Reportes y métricas
       ========================== */

    // Géneros sin canciones registradas
    @Query("""
        SELECT g
        FROM Genero g
        WHERE g.canciones IS EMPTY
    """)
    List<Genero> generosSinCanciones();

    // Conteo de canciones por género
    @Query("""
        SELECT g.nombre AS genero, COUNT(c) AS totalCanciones
        FROM Genero g
        LEFT JOIN g.canciones c
        GROUP BY g.nombre
        ORDER BY totalCanciones DESC
    """)
    List<Object[]> reporteConteoCancionesPorGenero();

    /* ==========================
       Búsquedas avanzadas
       ========================== */

    // Buscar género que contenga una canción con cierto título
    @Query("""
        SELECT DISTINCT g
        FROM Genero g
        JOIN g.canciones c
        WHERE LOWER(c.titulo) LIKE LOWER(CONCAT('%', :titulo, '%'))
    """)
    List<Genero> buscarPorTituloDeCancion(@Param("titulo") String titulo);
}
