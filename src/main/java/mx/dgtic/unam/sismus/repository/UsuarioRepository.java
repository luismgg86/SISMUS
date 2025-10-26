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

    Optional<Usuario> findByNickname(String nickname);
    Optional<Usuario> findByCorreo(String correo);
    List<Usuario> findByNombreContainingIgnoreCase(String nombre);
    Page<Usuario> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    @EntityGraph(attributePaths = {"listas", "descargas"})
    @Query("SELECT u FROM Usuario u WHERE u.id = :id")
    Optional<Usuario> findByIdConRelaciones(@Param("id") Integer id);

    @Query("""
        SELECT DISTINCT u
        FROM Usuario u
        WHERE u.listas IS NOT EMPTY
    """)
    List<Usuario> usuariosConListas();

    @Query("""
        SELECT DISTINCT u
        FROM Usuario u
        WHERE u.cancionesDescargadas IS EMPTY
    """)
    List<Usuario> usuariosSinDescargas();

    @Query("""
        SELECT u.nickname AS usuario, COUNT(l) AS totalListas
        FROM Usuario u
        LEFT JOIN u.listas l
        GROUP BY u.nickname
        ORDER BY totalListas DESC
    """)
    List<Object[]> reporteTopUsuariosPorListas();

    @Query("""
        SELECT u.nickname AS usuario,
               COUNT(DISTINCT d) AS totalDescargas,
               COUNT(DISTINCT l) AS totalListas,
               u.ultimoAcceso AS ultimoAcceso
        FROM Usuario u
        LEFT JOIN u.cancionesDescargadas d
        LEFT JOIN u.listas l
        GROUP BY u.nickname, u.ultimoAcceso
        ORDER BY totalDescargas DESC
    """)
    List<Object[]> reporteActividadUsuarios();

    @Query("""
        SELECT u.nickname AS usuario, COUNT(d) AS totalDescargas
        FROM Usuario u
        LEFT JOIN u.cancionesDescargadas d
        GROUP BY u.nickname
        ORDER BY totalDescargas DESC
    """)
    List<Object[]> reporteTopUsuariosPorDescargas();

    @Query("""
        SELECT u
        FROM Usuario u
        WHERE LOWER(u.nombre) LIKE LOWER(CONCAT('%', :filtro, '%'))
           OR LOWER(u.correo) LIKE LOWER(CONCAT('%', :filtro, '%'))
    """)
    List<Usuario> buscarPorNombreOCorreo(@Param("filtro") String filtro);
}
