package mx.dgtic.unam.sismus.repository;

import mx.dgtic.unam.sismus.model.Cancion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CancionRepository extends JpaRepository<Cancion, Integer> {

    // Buscar canciones por nombre de artista (case-insensitive)
    List<Cancion> findByArtista_NombreContainingIgnoreCase(String nombreArtista);

    // Buscar canciones por clave de género
    List<Cancion> findByGenero_Clave(String claveGenero);

    // Buscar canciones en una lista específica
    List<Cancion> findByListas_Id(Integer listaId);

    // Paginado para catálogos
    Page<Cancion> findByTituloContainingIgnoreCase(String titulo, Pageable pageable);

    // Traer canciones con artista y género
    @EntityGraph(attributePaths = {"artista", "genero"})
    @Query("SELECT c FROM Cancion c")
    List<Cancion> findAllConArtistaYGenero();

    //Traer canciones activas
    @Query("SELECT c FROM Cancion c WHERE c.activo = true AND LOWER(c.titulo) LIKE LOWER(CONCAT('%', :titulo, '%'))")
    Page<Cancion> buscarPorTituloActivo(@Param("titulo") String titulo, Pageable pageable);

    @Query("""
    SELECT c
    FROM Cancion c
    WHERE c.activo = true
      AND c.artista.activo = true
      AND (:titulo IS NULL OR LOWER(c.titulo) LIKE LOWER(CONCAT('%', :titulo, '%')))
""")
    Page<Cancion> buscarActivasConArtistaActivo(@Param("titulo") String titulo, Pageable pageable);

    Page<Cancion> findByActivoTrue(Pageable pageable);

    List<Cancion> findByActivoTrue();

    List<Cancion> findByActivoFalse();

    // Buscar una canción por ID cargando artista y género
    @EntityGraph(attributePaths = {"artista", "genero"})
    @Query("SELECT c FROM Cancion c WHERE c.id = :id")
    Cancion findByIdConRelaciones(@Param("id") Integer id);

    // Nativa: buscar por nombre de artista
    @Query(value = """
      select c.*
      from cancion c
      join artista a on a.artista_id = c.artista_id
      where lower(a.nombre) like lower(concat('%', :nombre, '%'))
    """, nativeQuery = true)
    List<Cancion> buscarPorNombreArtista(@Param("nombre") String nombre);

    // Buscar por clave de género
    @Query("""
        select c
        from Cancion c
        join c.genero g
        where g.clave = :clave
    """)
    List<Cancion> buscarPorClaveGenero(@Param("clave") String clave);

    // Nativa: buscar por artista y género
    @Query(value = """
        select c.*
        from cancion c
        join artista a on a.artista_id = c.artista_id
        join genero g on g.genero_id = c.genero_id
        where a.clave = :claveArtista
        and g.clave = :claveGenero
    """, nativeQuery = true)
    List<Cancion> nativaPorArtistaYGenero(@Param("claveArtista") String claveArtista,
                                          @Param("claveGenero") String claveGenero);

    //Consultas creadas para los reportes que se podrán descargar

    // Canciones sin lista asignada (registro incompleto)
    @Query("""
        select c
        from Cancion c
        where c.listas is empty
    """)
    List<Cancion> cancionesSinLista();

    // Conteo de canciones por género
    @Query("""
        select g.nombre as genero, count(c) as total
        from Cancion c
        join c.genero g
        group by g.nombre
        order by total desc
    """)
    List<Object[]> reporteConteoPorGenero();

    // Reporte: canciones más descargadas
    @Query("""
        select c.titulo AS titulo, COUNT(u) AS totalDescargas
        from Cancion c
        join c.descargas u
        group by c.titulo
        order by totalDescargas DESC
    """)
    List<Object[]> reporteCancionesMasDescargadas();

    // Buscar canciones con mínimo número de descargas
    @Query("""
        select c
        from Cancion c
        where size(c.descargas) >= :minimo
    """)
    List<Cancion> cancionesConMinimoDescargas(@Param("minimo") int minimo);

    // Creada como otra opción a implementar en el buscador

    // Búsqueda por artista y título parcial
    @Query("""
        select c
        from Cancion c
        join c.artista a
        where lower(a.nombre) like lower(concat('%', :artista, '%'))
        and lower(c.titulo) like lower(concat('%', :titulo, '%'))
    """)
    List<Cancion> buscarPorArtistaYTitulo(@Param("artista") String artista,
                                          @Param("titulo") String titulo);
}
