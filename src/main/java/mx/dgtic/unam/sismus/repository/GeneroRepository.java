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

    Optional<Genero> findByClave(String clave);
    List<Genero> findByNombreContainingIgnoreCase(String nombre);
    Page<Genero> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
    boolean existsByClave(String clave);
    List<Genero> findAllByOrderByNombreAsc();

    @EntityGraph(attributePaths = "canciones")
    @Query("SELECT g FROM Genero g WHERE g.clave = :clave")
    Optional<Genero> detalleConCancionesPorClave(@Param("clave") String clave);

    @Query("SELECT g FROM Genero g WHERE g.canciones IS EMPTY")
    List<Genero> generosSinCanciones();

    @Query("""
        SELECT g.nombre AS genero, COUNT(c) AS totalCanciones
        FROM Genero g
        LEFT JOIN g.canciones c
        GROUP BY g.nombre
        ORDER BY totalCanciones DESC
    """)
    List<Object[]> reporteConteoCancionesPorGenero();
}
