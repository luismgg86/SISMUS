package mx.dgtic.unam.sismus.repository;

import mx.dgtic.unam.sismus.model.Cancion;
import mx.dgtic.unam.sismus.model.UsuarioCancion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioCancionRepository extends JpaRepository<UsuarioCancion, Integer> {

    @Query("""
        SELECT uc.cancion
        FROM UsuarioCancion uc
        WHERE uc.usuario.nickname = :nickname
        GROUP BY uc.cancion
        ORDER BY COUNT(uc) DESC
    """)
    List<Cancion> top10CancionesMasDescargadas(String nickname);

    @Query("""
        SELECT c.artista, COUNT(uc)
        FROM UsuarioCancion uc
        JOIN uc.cancion c
        WHERE c.artista IS NOT NULL
        GROUP BY c.artista
        ORDER BY COUNT(uc) DESC
    """)
    List<Object[]> topArtistasMasDescargados();

    @Query("""
        SELECT c.genero, COUNT(uc)
        FROM UsuarioCancion uc
        JOIN uc.cancion c
        WHERE c.genero IS NOT NULL
        GROUP BY c.genero
        ORDER BY COUNT(uc) DESC
    """)
    List<Object[]> topGenerosMasDescargados();
}
