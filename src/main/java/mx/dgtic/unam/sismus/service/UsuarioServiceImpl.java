package mx.dgtic.unam.sismus.service;

import mx.dgtic.unam.sismus.dto.UsuarioRegistroDto;
import mx.dgtic.unam.sismus.dto.UsuarioResponseDto;
import mx.dgtic.unam.sismus.exception.UsuarioNoEncontradoException;
import mx.dgtic.unam.sismus.mapper.UsuarioMapper;
import mx.dgtic.unam.sismus.model.Rol;
import mx.dgtic.unam.sismus.model.Usuario;
import mx.dgtic.unam.sismus.repository.RolRepository;
import mx.dgtic.unam.sismus.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder; // ✅

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              RolRepository rolRepository,
                              UsuarioMapper usuarioMapper,
                              PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UsuarioResponseDto registrar(UsuarioRegistroDto dto) {
        Rol rolUser = rolRepository.findByNombre("USER")
                .orElseThrow(() -> new RuntimeException("No existe el rol USER"));

        Usuario usuario = usuarioMapper.toEntity(dto, rolUser);
        return usuarioMapper.toResponseDto(usuarioRepository.save(usuario));
    }

    @Override
    public UsuarioResponseDto actualizar(Integer id, UsuarioRegistroDto dto) {
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));

        existente.setNombre(dto.getNombre());
        existente.setApPaterno(dto.getApPaterno());
        existente.setApMaterno(dto.getApMaterno());
        existente.setCorreo(dto.getCorreo());
        existente.setNickname(dto.getNickname());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            existente.setPassword(passwordEncoder.encode(dto.getPassword())); // ✅ Encripta
        }

        return usuarioMapper.toResponseDto(usuarioRepository.save(existente));
    }

    public void eliminar(Integer id) {
        if (!usuarioRepository.existsById(id))
            throw new UsuarioNoEncontradoException("Usuario no encontrado");
        usuarioRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Optional<UsuarioResponseDto> buscarPorId(Integer id) {
        return usuarioRepository.findById(id).map(usuarioMapper::toResponseDto);
    }

    @Transactional(readOnly = true)
    public Optional<UsuarioResponseDto> buscarPorNickname(String nickname) {
        return usuarioRepository.findByNickname(nickname).map(usuarioMapper::toResponseDto);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDto> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(usuarioMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponseDto> listarPaginado(String filtro, Pageable pageable) {
        return usuarioRepository.findByNombreContainingIgnoreCase(filtro, pageable)
                .map(usuarioMapper::toResponseDto);
    }

    @Override
    public void actualizarPassword(Integer idUsuario, String nuevaPassword) {
        var usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(()-> new UsuarioNoEncontradoException("Usuario con Id: " + idUsuario + " no encontrado"));

        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);
    }

    @Override
    public Optional<Usuario> buscarUsuarioPorNickname(String nickname) {
        return usuarioRepository.findByNickname(nickname);
    }

    @Override
    public Optional<Usuario> buscarUsuarioPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }
}
