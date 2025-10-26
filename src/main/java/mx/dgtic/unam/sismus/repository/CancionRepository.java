package mx.dgtic.unam.sismus.repository;

import mx.dgtic.unam.sismus.model.Cancion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CancionRepository extends JpaRepository<Cancion, Integer> {

    List<Cancion> findByActivoTrue();
    List<Cancion> findByActivoFalse();

    @Query("""
        SELECT c
        FROM Cancion c
        WHERE c.activo = true
          AND c.artista.activo = true
          AND (:titulo IS NULL OR LOWER(c.titulo) LIKE LOWER(CONCAT('%', :titulo, '%')))
    """)
    Page<Cancion> buscarActivasConArtistaActivo(@Param("titulo") String titulo, Pageable pageable);

    @Query("""
        SELECT c
        FROM Cancion c
        WHERE c.activo = true
          AND c.artista.activo = true
          AND (
              LOWER(c.titulo) LIKE LOWER(CONCAT('%', :query, '%'))
              OR LOWER(c.artista.nombre) LIKE LOWER(CONCAT('%', :query, '%'))
              OR LOWER(c.genero.nombre) LIKE LOWER(CONCAT('%', :query, '%'))
          )
    """)
    Page<Cancion> buscarPorTituloArtistaGeneroActivoPaginado(@Param("query") String query, Pageable pageable);
}
