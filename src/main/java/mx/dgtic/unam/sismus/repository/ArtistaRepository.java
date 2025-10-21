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

    // Buscar artista por clave exacta
    Optional<Artista> findByClave(String clave);

    // Buscar por nombre
    List<Artista> findByNombreContainingIgnoreCase(String nombre);

    // Paginado para catálogos
    Page<Artista> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    // Verificar si existe un artista con cierta clave
    boolean existsByClave(String clave);

    // Traer artista con sus canciones en una sola query
    @EntityGraph(attributePaths = "canciones")
    @Query("select a from Artista a where a.clave = :clave")
    Optional<Artista> detalleConCancionesPorClave(@Param("clave") String clave);

    // Listar todos los artistas con sus canciones
    @EntityGraph(attributePaths = "canciones")
    @Query("select a from Artista a")
    List<Artista> findAllConCanciones();

    List<Artista> findByActivoTrue();

    List<Artista> findByActivoFalse();

    /* ==========================
       Reportes y estadísticas
       ========================== */

    // Reporte: artistas sin canciones (posibles registros incompletos)
    @Query("select a from Artista a where a.canciones is empty")
    List<Artista> artistasSinCanciones();

    // Reporte: artistas con conteo de canciones
    @Query("""
        select a.nombre as nombre,
               count(c) as totalCanciones
        from Artista a
        left join a.canciones c
        group by a.nombre
        order by totalCanciones desc
    """)
    List<Object[]> reporteArtistasPorTotalCanciones();

    // Reporte: artistas con mayor número de descargas
    @Query("""
        select a.nombre as nombre,
               count(uc) as totalDescargas
        from Artista a
        join a.canciones c
        join c.descargas uc
        group by a.nombre
        order by totalDescargas desc
    """)
    List<Object[]> reporteArtistasMasDescargados();

    /* ==========================
       Búsqueda avanzada por canciones
       ========================== */

    // Buscar artistas por título de canción
    @Query("""
        select distinct a
        from Artista a
        join a.canciones c
        where lower(c.titulo) like lower(concat('%', :titulo, '%'))
    """)
    List<Artista> buscarPorTituloDeCancion(@Param("titulo") String titulo);

    // Buscar artistas con canciones de un género específico
    @Query("""
        select distinct a
        from Artista a
        join a.canciones c
        where lower(c.genero.nombre) = lower(:genero)
    """)
    List<Artista> buscarPorGenero(@Param("genero") String genero);
}
