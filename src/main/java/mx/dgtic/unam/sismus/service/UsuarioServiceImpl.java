package mx.dgtic.unam.sismus.service;

import mx.dgtic.unam.sismus.dto.UsuarioRegistroDto;
import mx.dgtic.unam.sismus.dto.UsuarioResponseDto;
import mx.dgtic.unam.sismus.exception.UsuarioNoEncontradoException;
import mx.dgtic.unam.sismus.mapper.UsuarioMapper;
import mx.dgtic.unam.sismus.model.Usuario;
import mx.dgtic.unam.sismus.repository.UsuarioRepository;
import mx.dgtic.unam.sismus.service.UsuarioService;
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
    private final UsuarioMapper usuarioMapper;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
    }

    public UsuarioResponseDto registrar(UsuarioRegistroDto dto) {
        Usuario usuario = usuarioMapper.toEntity(dto);
        return usuarioMapper.toResponseDto(usuarioRepository.save(usuario));
    }

    public UsuarioResponseDto actualizar(Integer id, UsuarioRegistroDto dto) {
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));
        existente.setNombre(dto.getNombre());
        existente.setApPaterno(dto.getApPaterno());
        existente.setApMaterno(dto.getApMaterno());
        existente.setCorreo(dto.getCorreo());
        existente.setNickname(dto.getNickname());
        existente.setContrasena(dto.getContrasena());
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
}
