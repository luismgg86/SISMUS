package mx.dgtic.unam.sismus.service;

import mx.dgtic.unam.sismus.dto.UsuarioRegistroDto;
import mx.dgtic.unam.sismus.dto.UsuarioResponseDto;
import mx.dgtic.unam.sismus.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UsuarioService {

    UsuarioResponseDto registrar(UsuarioRegistroDto dto);
    UsuarioResponseDto actualizar(Integer id, UsuarioRegistroDto dto);
    void eliminar(Integer id);
    void cambiarEstado(Integer id, boolean activo);
    void actualizarRoles(Integer id, Set<String> roles);

    Optional<UsuarioResponseDto> buscarPorId(Integer id);
    Optional<UsuarioResponseDto> buscarPorNickname(String nickname);
    List<UsuarioResponseDto> listarTodos();
    Page<UsuarioResponseDto> listarPaginado(String filtro, Pageable pageable);

    //Solo para el cambio de contraseña, no se expone el front
    void actualizarPassword(Integer id, String nuevaPassword);
    Optional<Usuario> buscarUsuarioPorNickname(String nickname);
    Optional<Usuario> buscarUsuarioPorCorreo(String correo);

}
