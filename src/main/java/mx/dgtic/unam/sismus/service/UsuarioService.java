package mx.dgtic.unam.sismus.service;

import mx.dgtic.unam.sismus.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {

    Usuario crear(Usuario usuario);
    Usuario actualizar(Usuario usuario);
    void eliminar(Integer id);

    Optional<Usuario> buscarPorId(Integer id);
    Optional<Usuario> buscarPorNickname(String nickname);
    Optional<Usuario> buscarPorCorreo(String correo);

    List<Usuario> listarTodos();
    Page<Usuario> listarPaginado(String filtroNombre, Pageable pageable);

    List<Usuario> usuariosConListas();
    List<Usuario> usuariosSinDescargas();

    List<Object[]> reporteTopUsuariosPorListas();
    List<Object[]> reporteTopUsuariosPorDescargas();

    List<Usuario> buscarPorNombreOCorreo(String filtro);
}
