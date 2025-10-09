package mx.dgtic.unam.sismus.repository;

import mx.dgtic.unam.sismus.model.Lista;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ListaRepository extends JpaRepository<Lista, Integer> {

    // Buscar listas por nickname de usuario
    List<Lista> findByUsuario_Nickname(String nickname);

    // Buscar listas por ID de usuario
    List<Lista> findByUsuario_Id(Integer id);

    // Búsqueda parcial por nombre
    List<Lista> findByNombreContainingIgnoreCase(String nombre);

    // Paginación para catálogos
    Page<Lista> findByUsuario_Nickname(String nickname, Pageable pageable);

    // Traer lista con canciones y usuario en una sola query
    @EntityGraph(attributePaths = {"usuario", "canciones"})
    @Query("select l from Lista l where l.id = :id")
    Lista findByIdConRelaciones(@Param("id") Integer id);

    // Traer todas las listas con canciones y usuario
    @EntityGraph(attributePaths = {"usuario", "canciones"})
    @Query("select l from Lista l")
    List<Lista> findAllConRelaciones();

    // Buscar listas por correo de usuario
    @Query("""
        select l
        from Lista l
        join l.usuario u
        where u.correo = :correo
    """)
    List<Lista> buscarPorCorreoUsuario(@Param("correo") String correo);

    // JPQL: Buscar listas que contienen canciones con un título específico
    @Query("""
        select DISTINCT l
        from Lista l
        join l.canciones c
        where lower(c.titulo) like lower(concat('%', :titulo, '%'))
    """)
    List<Lista> buscarPorTituloDeCancion(@Param("titulo") String titulo);

    //Consultas para reportes

    // Listas sin canciones registradas
    @Query("""
        select l
        from Lista l
        where l.canciones IS EMPTY
    """)
    List<Lista> listasVacias();

    // Reporte: cantidad de canciones por lista
    @Query("""
        select l.nombre AS nombre, count(c) AS totalCanciones
        from Lista l
        left join l.canciones c
        group by l.nombre
        order by totalCanciones DESC
    """)
    List<Object[]> reporteConteoCancionesPorLista();

    // Reporte: top listas con más canciones
    @Query("""
        select l.nombre AS nombre, count(c) AS totalCanciones
        from Lista l
        left join l.canciones c
        group by l.id, l.nombre
        order by totalCanciones DESC
    """)
    List<Object[]> topListasMasPopulares();

    // Buscar listas por usuario y nombre parcial
    @Query("""
        select l
        from Lista l
        join l.usuario u
        where lower(u.nickname) = lower(:nickname)
        AND lower(l.nombre) like lower(concat('%', :nombre, '%'))
    """)
    List<Lista> buscarPorUsuarioYNombre(@Param("nickname") String nickname,
                                        @Param("nombre") String nombre);
}
