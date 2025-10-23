package mx.dgtic.unam.sismus.service;

import mx.dgtic.unam.sismus.dto.CancionResponseDto;
import mx.dgtic.unam.sismus.dto.UsuarioCancionDto;
import mx.dgtic.unam.sismus.exception.CancionNoEncontradaException;
import mx.dgtic.unam.sismus.exception.UsuarioNoEncontradoException;
import mx.dgtic.unam.sismus.mapper.UsuarioCancionMapper;
import mx.dgtic.unam.sismus.model.Cancion;
import mx.dgtic.unam.sismus.model.Lista;
import mx.dgtic.unam.sismus.model.Usuario;
import mx.dgtic.unam.sismus.model.UsuarioCancion;
import mx.dgtic.unam.sismus.repository.CancionRepository;
import mx.dgtic.unam.sismus.repository.UsuarioCancionRepository;
import mx.dgtic.unam.sismus.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class UsuarioCancionServiceImpl implements UsuarioCancionService {

    private final UsuarioCancionRepository usuarioCancionRepository;
    private final UsuarioRepository usuarioRepository;
    private final CancionRepository cancionRepository;
    private final UsuarioCancionMapper usuarioCancionMapper;

    public UsuarioCancionServiceImpl(UsuarioCancionRepository usuarioCancionRepository,
                                     UsuarioRepository usuarioRepository,
                                     CancionRepository cancionRepository,
                                     UsuarioCancionMapper usuarioCancionMapper) {
        this.usuarioCancionRepository = usuarioCancionRepository;
        this.usuarioRepository = usuarioRepository;
        this.cancionRepository = cancionRepository;
        this.usuarioCancionMapper = usuarioCancionMapper;
    }

    public UsuarioCancionDto registrarDescarga(Integer usuarioId, Integer cancionId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con ID " + usuarioId));
        Cancion cancion = cancionRepository.findById(cancionId)
                .orElseThrow(() -> new CancionNoEncontradaException("Canción no encontrada con ID " + cancionId));

        UsuarioCancion uc = new UsuarioCancion();
        uc.setUsuario(usuario);
        uc.setCancion(cancion);
        uc.setFechaDescarga(LocalDate.now());
        return usuarioCancionMapper.toDto(usuarioCancionRepository.save(uc));
    }

    @Transactional(readOnly = true)
    public List<UsuarioCancionDto> obtenerDescargasPorUsuario(Integer usuarioId) {
        return usuarioCancionRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(usuarioCancionMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UsuarioCancionDto> obtenerDescargasPorCancion(Integer cancionId) {
        return usuarioCancionRepository.findByCancionId(cancionId)
                .stream()
                .map(usuarioCancionMapper::toDto)
                .toList();
    }

    public void eliminarDescarga(Integer id) {
        if (!usuarioCancionRepository.existsById(id))
            throw new IllegalArgumentException("Descarga no encontrada con ID: " + id);
        usuarioCancionRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean existeDescarga(Integer usuarioId, Integer cancionId) {
        return usuarioCancionRepository.existsByUsuarioIdAndCancionId(usuarioId, cancionId);
    }

    @Override
    @Transactional
    public void registrarDescargas(Integer usuarioId, List<CancionResponseDto> canciones) {
        for (CancionResponseDto c : canciones) {
            registrarDescarga(usuarioId, c.getId());
        }
    }


}
