package mx.dgtic.unam.sismus.repository;

import mx.dgtic.unam.sismus.model.Cancion;
import mx.dgtic.unam.sismus.model.UsuarioCancion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface UsuarioCancionRepository extends JpaRepository<UsuarioCancion, Integer> {

    /* ==========================
       Búsquedas básicas
       ========================== */

    // Buscar descargas por ID de usuario
    List<UsuarioCancion> findByUsuario_Id(Integer id);

    // Buscar descargas por título parcial de canción
    List<UsuarioCancion> findByCancion_TituloContainingIgnoreCase(String titulo);

    // Paginación de descargas por usuario (por nickname)
    Page<UsuarioCancion> findByUsuario_Nickname(String nickname, Pageable pageable);

    // Verificar si un usuario ya descargó una canción (usado por el servicio)
    boolean existsByUsuarioIdAndCancionId(Integer usuarioId, Integer cancionId);

    List<UsuarioCancion> findByCancionId(Integer cancionId);

    List<UsuarioCancion> findByUsuarioId(Integer usuarioId);


    /* ==========================
       Cargas con relaciones
       ========================== */

    // Traer usuario y canción en una sola query (evita lazy loading)
    @EntityGraph(attributePaths = {"usuario", "cancion"})
    @Query("select uc from UsuarioCancion uc where uc.usuario.nickname = :nickname")
    List<UsuarioCancion> findByNicknameConRelaciones(@Param("nickname") String nickname);


    /* ==========================
       Consultas personalizadas
       ========================== */

    // Buscar descargas por nickname de usuario
    @Query("""
        select uc
        from UsuarioCancion uc
        join uc.usuario u
        where u.nickname = :nickname
    """)
    List<UsuarioCancion> buscarPorNicknameUsuario(@Param("nickname") String nickname);

    // Buscar descargas por nombre de artista (nativa)
    @Query(value = """
        select uc.*
        from usuario_cancion uc
        join cancion c on c.cancion_id = uc.cancion_id
        join artista a on a.artista_id = c.artista_id
        where lower(a.nombre) like lower(concat('%', :nombre, '%'))
    """, nativeQuery = true)
    List<UsuarioCancion> buscarPorNombreArtista(@Param("nombre") String nombre);

    // Solo canciones descargadas por usuario (devuelve Canciones)
    @Query("""
        select uc.cancion
        from UsuarioCancion uc
        join uc.usuario u
        where u.nickname = :nickname
    """)
    List<Cancion> cancionesDescargadasPorUsuario(@Param("nickname") String nickname);


    /* ==========================
       🔹 Reportes y métricas
       ========================== */

    // Reporte: usuarios con más descargas
    @Query("""
        select u.nickname as usuario, count(uc) as totalDescargas
        from UsuarioCancion uc
        join uc.usuario u
        group by u.nickname
        order by totalDescargas desc
    """)
    List<Object[]> reporteUsuariosMasActivos();

    // Reporte: canciones más descargadas
    @Query("""
        select c.titulo as cancion, count(uc) as totalDescargas
        from UsuarioCancion uc
        join uc.cancion c
        group by c.titulo
        order by totalDescargas desc
    """)
    List<Object[]> reporteCancionesMasDescargadas();

    // Descargas en un rango de fechas
    List<UsuarioCancion> findByFechaDescargaBetween(LocalDate inicio, LocalDate fin);

    // Conteo total de descargas por usuario
    @Query("""
        select count(uc)
        from UsuarioCancion uc
        where uc.usuario.nickname = :nickname
    """)
    long conteoDescargasPorUsuario(@Param("nickname") String nickname);


    /* ==========================
        Filtros combinados
       ========================== */

    // Buscar por usuario (nickname) y título parcial
    @Query("""
        select uc
        from UsuarioCancion uc
        join uc.usuario u
        join uc.cancion c
        where lower(u.nickname) = lower(:nickname)
        and lower(c.titulo) like lower(concat('%', :titulo, '%'))
    """)
    List<UsuarioCancion> buscarPorUsuarioYTitulo(@Param("nickname") String nickname,
                                                 @Param("titulo") String titulo);
}
