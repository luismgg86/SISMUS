package mx.dgtic.unam.sismus.service;

import mx.dgtic.unam.sismus.model.Usuario;
import mx.dgtic.unam.sismus.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Usuario crear(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario actualizar(Usuario usuario) {
        if (!usuarioRepository.existsById(usuario.getId())) {
            throw new IllegalArgumentException("Usuario no encontrado con id: " + usuario.getId());
        }
        return usuarioRepository.save(usuario);
    }

    @Override
    public void eliminar(Integer id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Integer id) {
        return usuarioRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorNickname(String nickname) {
        return usuarioRepository.findByNickname(nickname);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Usuario> listarPaginado(String filtroNombre, Pageable pageable) {
        return usuarioRepository.findByNombreContainingIgnoreCase(filtroNombre, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> usuariosConListas() {
        return usuarioRepository.usuariosConListas();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> usuariosSinDescargas() {
        return usuarioRepository.usuariosSinDescargas();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> reporteTopUsuariosPorListas() {
        return usuarioRepository.reporteTopUsuariosPorListas();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> reporteTopUsuariosPorDescargas() {
        return usuarioRepository.reporteTopUsuariosPorDescargas();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> buscarPorNombreOCorreo(String filtro) {
        return usuarioRepository.buscarPorNombreOCorreo(filtro);
    }
}
