package mx.dgtic.unam.sismus.service;

import mx.dgtic.unam.sismus.dto.ArtistaDto;
import mx.dgtic.unam.sismus.dto.CancionResponseDto;
import mx.dgtic.unam.sismus.dto.GeneroDto;
import mx.dgtic.unam.sismus.dto.UsuarioCancionDto;
import mx.dgtic.unam.sismus.exception.CancionNoEncontradaException;
import mx.dgtic.unam.sismus.exception.UsuarioNoEncontradoException;
import mx.dgtic.unam.sismus.mapper.UsuarioCancionMapper;
import mx.dgtic.unam.sismus.model.*;
import mx.dgtic.unam.sismus.repository.CancionRepository;
import mx.dgtic.unam.sismus.repository.UsuarioCancionRepository;
import mx.dgtic.unam.sismus.repository.UsuarioRepository;
import org.springframework.security.core.context.SecurityContextHolder;
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
    @Override
    public List<CancionResponseDto> obtenerTop10CancionesMasDescargadas() {

        String nickname = SecurityContextHolder.getContext().getAuthentication().getName();

        return usuarioCancionRepository.top10CancionesMasDescargadas(nickname)
                .stream()
                .limit(10)
                .map(cancion -> new CancionResponseDto(
                        cancion.getId(),
                        cancion.getTitulo(),
                        cancion.getAudio(),
                        cancion.getDuracion(),
                        cancion.isActivo(),
                        cancion.getFechaAlta(),
                        cancion.getArtista() != null ? cancion.getArtista().getArtistaId() : null,
                        cancion.getArtista() != null ? cancion.getArtista().getNombre() : null,
                        cancion.getGenero() != null ? cancion.getGenero().getId() : null,
                        cancion.getGenero() != null ? cancion.getGenero().getNombre() : null,
                        null
                ))
                .toList();
    }

    @Override
    @Transactional
    public List<ArtistaDto> obtenerTopArtistasMasDescargados() {
        var resultados = usuarioCancionRepository.topArtistasMasDescargados();

        return resultados.stream()
                .map(fila -> {
                    Artista artista = (Artista) fila[0];
                    Long totalDescargas = (Long) fila[1];
                    ArtistaDto dto = new ArtistaDto();
                    dto.setId(artista.getArtistaId());
                    dto.setClave(artista.getClave());
                    dto.setNombre(artista.getNombre() + " (" + totalDescargas + " descargas)");
                    return dto;
                })
                .limit(10)
                .toList();
    }

    @Override
    @Transactional
    public List<GeneroDto> obtenerTopGenerosMasDescargados() {
        var resultados = usuarioCancionRepository.topGenerosMasDescargados();

        return resultados.stream()
                .map(fila -> {
                    Genero genero = (Genero) fila[0];
                    Long totalDescargas = (Long) fila[1];
                    GeneroDto dto = new GeneroDto();
                    dto.setId(genero.getId());
                    dto.setNombre(genero.getNombre() + " (" + totalDescargas + " descargas)");
                    dto.setClave(genero.getClave());
                    return dto;
                })
                .limit(10)
                .toList();
    }


}
