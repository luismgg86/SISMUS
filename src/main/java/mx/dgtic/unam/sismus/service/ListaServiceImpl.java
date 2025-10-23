package mx.dgtic.unam.sismus.service;

import jakarta.servlet.http.HttpServletResponse;
import mx.dgtic.unam.sismus.dto.*;
import mx.dgtic.unam.sismus.exception.*;
import mx.dgtic.unam.sismus.mapper.ListaMapper;
import mx.dgtic.unam.sismus.model.*;
import mx.dgtic.unam.sismus.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Transactional
public class ListaServiceImpl implements ListaService {

    private final ListaRepository listaRepository;
    private final CancionRepository cancionRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final UsuarioCancionService usuarioCancionService;
    private final ListaMapper listaMapper;

    public ListaServiceImpl(
            ListaRepository listaRepository,
            CancionRepository cancionRepository,
            UsuarioRepository usuarioRepository,
            UsuarioService usuarioService,
            UsuarioCancionService usuarioCancionService,
            ListaMapper listaMapper
    ) {
        this.listaRepository = listaRepository;
        this.cancionRepository = cancionRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
        this.usuarioCancionService = usuarioCancionService;
        this.listaMapper = listaMapper;
    }

    // ✅ Crear una nueva playlist
    @Override
    public ListaResponseDto crearPlaylist(ListaRequestDto dto) {
        Usuario usuario = usuarioRepository.getReferenceById(dto.getUsuarioId());

        Lista lista = listaMapper.toEntity(dto, usuario);
        lista.setFechaCreacion(LocalDate.now());

        lista = listaRepository.save(lista);
        return listaMapper.toResponseDto(lista);
    }

    // ✅ Obtener playlist con relaciones
    @Override
    public ListaResponseDto obtenerConRelaciones(Integer id) {
        Lista lista = listaRepository.findByIdConRelaciones(id);
        if (lista == null)
            throw new ListaNoEncontradaException("Lista no encontrada con ID: " + id);

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

    // ✅ Obtener playlists por usuario
    @Override
    public List<ListaResponseDto> obtenerListasPorUsuario(String nickname) {
        return listaRepository.findByUsuario_Nickname(nickname)
                .stream()
                .map(listaMapper::toResponseDto)
                .toList();
    }

    // ✅ Agregar canción a una lista
    @Override
    public void agregarCancionALista(Integer listaId, Integer cancionId) {
        Lista lista = listaRepository.findByIdConRelaciones(listaId);
        if (lista == null)
            throw new ListaNoEncontradaException("Lista no encontrada " + listaId);

        Cancion cancion = cancionRepository.findById(cancionId)
                .orElseThrow(() -> new CancionNoEncontradaException("Canción no encontrada " + cancionId));

        boolean existe = lista.getCanciones().stream()
                .anyMatch(lc -> lc.getCancion().getId().equals(cancionId));
        if (existe)
            throw new CancionDuplicadaException("La canción ya está en la playlist.");

        ListaCancion lc = new ListaCancion();
        lc.setLista(lista);
        lc.setCancion(cancion);
        lc.setFechaAgregada(LocalDate.now());

        lista.getCanciones().add(lc);
        listaRepository.save(lista);
    }

    // ✅ Eliminar canción de la lista
    @Override
    public void eliminarCancionDeLista(Integer listaId, Integer cancionId) {
        Lista lista = listaRepository.findByIdConRelaciones(listaId);
        if (lista == null)
            throw new ListaNoEncontradaException("Lista no encontrada.");

        lista.getCanciones().removeIf(lc -> lc.getCancion().getId().equals(cancionId));
        listaRepository.save(lista);
    }

    // ✅ Eliminar una playlist completa
    @Override
    public void eliminar(Integer id) {
        if (!listaRepository.existsById(id))
            throw new ListaNoEncontradaException("No existe la lista con ID: " + id);
        listaRepository.deleteById(id);
    }

    // ✅ Descargar playlist como ZIP y registrar descargas
    @Override
    public void descargarPlaylistComoZip(Integer playlistId, String nickname, HttpServletResponse response) throws IOException {
        ListaResponseDto playlist = obtenerConRelaciones(playlistId);

        if (playlist.getCanciones() == null || playlist.getCanciones().isEmpty())
            throw new RuntimeException("La playlist está vacía.");

        // 🔹 Filtrar canciones activas
        List<CancionResponseDto> cancionesActivas = playlist.getCanciones().stream()
                .filter(CancionResponseDto::getActivo)
                .toList();

        if (cancionesActivas.isEmpty())
            throw new RuntimeException("No hay canciones activas para descargar.");

        // 🔹 Registrar descargas si hay usuario autenticado
        if (nickname != null) {
            usuarioService.buscarPorNickname(nickname).ifPresent(usuarioDto ->
                    usuarioCancionService.registrarDescargas(usuarioDto.getId(), cancionesActivas)
            );
        }

        // 🔹 Generar ZIP con las canciones
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + playlist.getNombre() + ".zip\"");

        try (ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())) {
            for (CancionResponseDto cancion : cancionesActivas) {
                Path path = Paths.get("src/main/resources/static/" + cancion.getAudio());
                if (Files.exists(path)) {
                    zos.putNextEntry(new ZipEntry(Path.of(cancion.getAudio()).getFileName().toString()));
                    Files.copy(path, zos);
                    zos.closeEntry();
                }
            }
        }
    }
}
