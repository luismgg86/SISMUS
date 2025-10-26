package mx.dgtic.unam.sismus.repository;

import mx.dgtic.unam.sismus.model.Lista;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ListaRepository extends JpaRepository<Lista, Integer> {

    List<Lista> findByUsuario_Nickname(String nickname);
    List<Lista> findByUsuario_Id(Integer usuarioId);

    @Query("""
        SELECT l
        FROM Lista l
        WHERE LOWER(l.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))
    """)
    List<Lista> buscarPorNombre(@Param("nombre") String nombre);

    @Query("""
        SELECT l
        FROM Lista l
        JOIN l.usuario u
        WHERE LOWER(u.nickname) = LOWER(:nickname)
          AND LOWER(l.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))
    """)
    List<Lista> buscarPorUsuarioYNombre(@Param("nickname") String nickname,
                                        @Param("nombre") String nombre);

    @Query("""
        SELECT l
        FROM Lista l
        LEFT JOIN FETCH l.canciones lc
        LEFT JOIN FETCH lc.cancion
        WHERE l.id = :id
    """)
    Lista findByIdConRelaciones(@Param("id") Integer id);

    @EntityGraph(attributePaths = {"usuario", "canciones"})
    @Query("SELECT l FROM Lista l")
    List<Lista> findAllConRelaciones();

    @Query("""
        SELECT l
        FROM Lista l
        WHERE l.canciones IS EMPTY
    """)
    List<Lista> listasVacias();

    @Query("""
        SELECT l.nombre AS nombre, COUNT(c) AS totalCanciones
        FROM Lista l
        LEFT JOIN l.canciones c
        GROUP BY l.nombre
        ORDER BY totalCanciones DESC
    """)
    List<Object[]> reporteConteoCancionesPorLista();

    @Query("""
        SELECT l.nombre AS nombre, COUNT(c) AS totalCanciones
        FROM Lista l
        LEFT JOIN l.canciones c
        GROUP BY l.id, l.nombre
        ORDER BY totalCanciones DESC
    """)
    List<Object[]> topListasMasPopulares();
}
