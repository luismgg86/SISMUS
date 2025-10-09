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

    // Paginación de descargas por usuario
    Page<UsuarioCancion> findByUsuario_Nickname(String nickname, Pageable pageable);

    // Traer usuario y canción en una sola query
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

    // Nativa: Buscar descargas por nombre de artista
    @Query(value = """
        select uc.*
        from usuario_cancion uc
        join cancion c on c.cancion_id = uc.cancion_id
        join artista a on a.artista_id = c.artista_id
        where lower(a.nombre) like lower(ConCAT('%', :nombre, '%'))
    """, nativeQuery = true)
    List<UsuarioCancion> buscarPorNombreArtista(@Param("nombre") String nombre);

    // Solo canciones descargadas por usuario
    @Query("""
        select uc.cancion
        from UsuarioCancion uc
        join uc.usuario u
        where u.nickname = :nickname
    """)
    List<Cancion> cancionesDescargadasPorUsuario(@Param("nickname") String nickname);

    /* ==========================
       Reportes y métricas
       ========================== */

    // Reporte: usuarios con más descargas
    @Query("""
        select u.nickname AS usuario, COUNT(uc) AS totalDescargas
        from UsuarioCancion uc
        join uc.usuario u
        group by u.nickname
        order by totalDescargas DESC
    """)
    List<Object[]> reporteUsuariosMasActivos();

    // Reporte: canciones más descargadas
    @Query("""
        select c.titulo AS cancion, COUNT(uc) AS totalDescargas
        from UsuarioCancion uc
        join uc.cancion c
        group by c.titulo
        order by totalDescargas DESC
    """)
    List<Object[]> reporteCancionesMasDescargadas();

    // Reporte: descargas en un rango de fechas
    List<UsuarioCancion> findByFechaDescargaBetween(LocalDate inicio, LocalDate fin);


    // Conteo total de descargas por usuario
    @Query("""
        select COUNT(uc)
        from UsuarioCancion uc
        where uc.usuario.nickname = :nickname
    """)
    long conteoDescargasPorUsuario(@Param("nickname") String nickname);

    /* ==========================
       Filtros combinados
       ========================== */

    // Buscar por usuario y título parcial
    @Query("""
        select uc
        from UsuarioCancion uc
        join uc.usuario u
        join uc.cancion c
        where lower(u.nickname) = lower(:nickname)
        and lower(c.titulo) like lower(ConCAT('%', :titulo, '%'))
    """)
    List<UsuarioCancion> buscarPorUsuarioYTitulo(@Param("nickname") String nickname,
                                                 @Param("titulo") String titulo);
}
