package mx.dgtic.unam.sismus.repository;

import mx.dgtic.unam.sismus.model.Artista;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ArtistaRepository extends JpaRepository<Artista, Integer> {

    Optional<Artista> findByClave(String clave);
    List<Artista> findByNombreContainingIgnoreCase(String nombre);
    Page<Artista> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
    boolean existsByClave(String clave);
    List<Artista> findByActivoTrue();
    List<Artista> findByActivoFalse();

    @EntityGraph(attributePaths = "canciones")
    @Query("SELECT a FROM Artista a WHERE a.clave = :clave")
    Optional<Artista> detalleConCancionesPorClave(@Param("clave") String clave);

    @Query("SELECT a FROM Artista a WHERE a.canciones IS EMPTY")
    List<Artista> artistasSinCanciones();

    @Query("""
        SELECT a.nombre AS nombre, COUNT(c) AS totalCanciones
        FROM Artista a
        LEFT JOIN a.canciones c
        GROUP BY a.nombre
        ORDER BY totalCanciones DESC
    """)
    List<Object[]> reporteArtistasPorTotalCanciones();

    @Query("""
        SELECT a.nombre AS nombre, COUNT(uc) AS totalDescargas
        FROM Artista a
        JOIN a.canciones c
        JOIN c.descargas uc
        GROUP BY a.nombre
        ORDER BY totalDescargas DESC
    """)
    List<Object[]> reporteArtistasMasDescargados();

    @EntityGraph(attributePaths = "canciones")
    @Query("select a from Artista a")
    List<Artista> findAllConCanciones();
}

