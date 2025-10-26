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

    List<UsuarioCancion> findByUsuarioId(Integer usuarioId);
    List<UsuarioCancion> findByCancionId(Integer cancionId);
    boolean existsByUsuarioIdAndCancionId(Integer usuarioId, Integer cancionId);
    Page<UsuarioCancion> findByUsuario_Nickname(String nickname, Pageable pageable);

    @EntityGraph(attributePaths = {"usuario", "cancion"})
    @Query("SELECT uc FROM UsuarioCancion uc WHERE uc.usuario.nickname = :nickname")
    List<UsuarioCancion> findByNicknameConRelaciones(@Param("nickname") String nickname);

    @Query("""
        SELECT uc.cancion
        FROM UsuarioCancion uc
        JOIN uc.usuario u
        WHERE u.nickname = :nickname
    """)
    List<Cancion> cancionesDescargadasPorUsuario(@Param("nickname") String nickname);

    @Query("""
        SELECT u.nickname AS usuario, COUNT(uc) AS totalDescargas
        FROM UsuarioCancion uc
        JOIN uc.usuario u
        GROUP BY u.nickname
        ORDER BY totalDescargas DESC
    """)
    List<Object[]> reporteUsuariosMasActivos();

    @Query("""
        SELECT c.titulo AS cancion, COUNT(uc) AS totalDescargas
        FROM UsuarioCancion uc
        JOIN uc.cancion c
        GROUP BY c.titulo
        ORDER BY totalDescargas DESC
    """)
    List<Object[]> reporteCancionesMasDescargadas();

    List<UsuarioCancion> findByFechaDescargaBetween(LocalDate inicio, LocalDate fin);

    @Query("""
        SELECT COUNT(uc)
        FROM UsuarioCancion uc
        WHERE uc.usuario.nickname = :nickname
    """)
    long conteoDescargasPorUsuario(@Param("nickname") String nickname);
}
