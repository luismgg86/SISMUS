package mx.dgtic.unam.sismus.service;

import jakarta.transaction.Transactional;
import mx.dgtic.unam.sismus.dto.ListaRequestDto;
import mx.dgtic.unam.sismus.dto.ListaResponseDto;
import mx.dgtic.unam.sismus.exception.CancionDuplicadaException;
import mx.dgtic.unam.sismus.exception.CancionNoEncontradaException;
import mx.dgtic.unam.sismus.exception.ListaNoEncontradaException;
import mx.dgtic.unam.sismus.exception.UsuarioNoEncontradoException;
import mx.dgtic.unam.sismus.mapper.ListaMapper;
import mx.dgtic.unam.sismus.model.Cancion;
import mx.dgtic.unam.sismus.model.Lista;
import mx.dgtic.unam.sismus.model.ListaCancion;
import mx.dgtic.unam.sismus.model.Usuario;
import mx.dgtic.unam.sismus.repository.CancionRepository;
import mx.dgtic.unam.sismus.repository.ListaRepository;
import mx.dgtic.unam.sismus.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class ListaServiceImpl implements ListaService {

    private final ListaRepository listaRepository;
    private final CancionRepository cancionRepository;
    private final UsuarioRepository usuarioRepository;
    private final ListaMapper listaMapper;

    public ListaServiceImpl(ListaRepository listaRepository,
                            CancionRepository cancionRepository,
                            UsuarioRepository usuarioRepository,
                            ListaMapper listaMapper) {
        this.listaRepository = listaRepository;
        this.cancionRepository = cancionRepository;
        this.usuarioRepository = usuarioRepository;
        this.listaMapper = listaMapper;
    }

    public ListaResponseDto crearPlaylist(ListaRequestDto dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado" + dto.getUsuarioId()));
        Lista lista = listaMapper.toEntity(dto, usuario);
        lista.setFechaCreacion(LocalDate.now());
        return listaMapper.toResponseDto(listaRepository.save(lista));
    }

    @Override
    public ListaResponseDto obtenerConRelaciones(Integer id) {
        Lista lista = listaRepository.findByIdConRelaciones(id);
        if (lista == null) throw new ListaNoEncontradaException("Lista no encontrada con ID: " + id);

        ListaResponseDto dto = listaMapper.toResponseDto(lista);

        dto.setCanciones(
                lista.getCanciones().stream()
                        .map(lc -> {
                            var cancionDto = listaMapper.mapCancion(lc.getCancion());
                            cancionDto.setFechaAgregada(lc.getFechaAgregada());
                            return cancionDto;
                        })
                        .toList()
        );
        return dto;
    }

    public List<ListaResponseDto> obtenerListasPorUsuario(String nickname) {
        return listaRepository.findByUsuario_Nickname(nickname)
                .stream()
                .map(listaMapper::toResponseDto)
                .toList();
    }

    public void agregarCancionALista(Integer listaId, Integer cancionId) {
        Lista lista = listaRepository.findByIdConRelaciones(listaId);
        if (lista == null) throw new ListaNoEncontradaException("Lista no encontrada " + listaId);
        Cancion cancion = cancionRepository.findById(cancionId)
                .orElseThrow(() -> new CancionNoEncontradaException("Canción no encontrada " + cancionId));
        boolean existe = lista.getCanciones().stream()
                .anyMatch(lc -> lc.getCancion().getId().equals(cancionId));
        if (existe) throw new CancionDuplicadaException("La canción ya está en la playlist");

        ListaCancion lc = new ListaCancion();
        lc.setLista(lista);
        lc.setCancion(cancion);
        lc.setFechaAgregada(LocalDate.now());
        lista.getCanciones().add(lc);
        listaRepository.save(lista);
    }

    public void eliminarCancionDeLista(Integer listaId, Integer cancionId) {
        Lista lista = listaRepository.findByIdConRelaciones(listaId);
        if (lista == null) throw new ListaNoEncontradaException("Lista no encontrada");
        lista.getCanciones().removeIf(lc -> lc.getCancion().getId().equals(cancionId));
        listaRepository.save(lista);
    }

    public void eliminar(Integer id) {
        if (!listaRepository.existsById(id))
            throw new ListaNoEncontradaException("No existe la lista con ID: " + id);
        listaRepository.deleteById(id);
    }
}
