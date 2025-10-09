package mx.dgtic.unam.sismus.repository;

import mx.dgtic.unam.sismus.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    /* ==========================
       Búsquedas básicas
       ========================== */

    // Buscar usuario por id
    Optional<Usuario> findById(Integer id);

    // Buscar usuario por nickname exacto
    Optional<Usuario> findByNickname(String nickname);

    // Buscar usuario por correo exacto
    Optional<Usuario> findByCorreo(String correo);

    // Buscar usuarios cuyo nombre contenga un texto (case-insensitive)
    List<Usuario> findByNombreContainingIgnoreCase(String nombre);

    // Paginación para listados grandes
    Page<Usuario> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    // Cargar usuario con sus listas y descargas en una sola query
    @EntityGraph(attributePaths = {"listas", "descargas"})
    @Query("select u from Usuario u where u.id = :id")
    Optional<Usuario> findByIdConRelaciones(@Param("id") Integer id);

    /* ==========================
       Consultas personalizadas
       ========================== */

    // Buscar usuarios con al menos una lista creada
    @Query("""
        select distinct u
        from Usuario u
        where u.listas is not empty
    """)
    List<Usuario> usuariosConListas();

    // Buscar usuarios que no han descargado canciones
    @Query("SELECT DISTINCT u FROM Usuario u WHERE u.cancionesDescargadas IS EMPTY")
    List<Usuario> usuariosSinDescargas();


    /* ==========================
       Reportes y métricas
       ========================== */

    // Top usuarios con más listas creadas
    @Query("""
        select u.nickname as usuario, count(l) as totalListas
        from Usuario u
        left join u.listas l
        group by u.nickname
        order by totalListas DESC
    """)
    List<Object[]> reporteTopUsuariosPorListas();

    // Top usuarios con más descargas
    @Query("""
    SELECT u.nickname AS usuario, COUNT(d) AS totalDescargas
    FROM Usuario u
    LEFT JOIN u.cancionesDescargadas d
    GROUP BY u.nickname
    ORDER BY totalDescargas DESC
    """)
    List<Object[]> reporteTopUsuariosPorDescargas();


    /* ==========================
       Filtros combinados
       ========================== */

    // Buscar por nombre o correo
    @Query("""
        select u
        from Usuario u
        where lower(u.nombre) like lower(concat('%', :filtro, '%'))
           or lower(u.correo) like lower(concat('%', :filtro, '%'))
    """)
    List<Usuario> buscarPorNombreOCorreo(@Param("filtro") String filtro);
}
