package mx.dgtic.unam.sismus.service;

import jakarta.servlet.http.HttpServletResponse;
import mx.dgtic.unam.sismus.dto.CancionResponseDto;
import mx.dgtic.unam.sismus.dto.ListaRequestDto;
import mx.dgtic.unam.sismus.dto.ListaResponseDto;
import mx.dgtic.unam.sismus.exception.*;
import mx.dgtic.unam.sismus.mapper.ListaMapper;
import mx.dgtic.unam.sismus.model.Cancion;
import mx.dgtic.unam.sismus.model.Lista;
import mx.dgtic.unam.sismus.model.ListaCancion;
import mx.dgtic.unam.sismus.model.Usuario;
import mx.dgtic.unam.sismus.repository.CancionRepository;
import mx.dgtic.unam.sismus.repository.ListaRepository;
import mx.dgtic.unam.sismus.repository.UsuarioRepository;
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

    public ListaServiceImpl(ListaRepository listaRepository,
                            CancionRepository cancionRepository,
                            UsuarioRepository usuarioRepository,
                            UsuarioService usuarioService,
                            UsuarioCancionService usuarioCancionService,
                            ListaMapper listaMapper) {
        this.listaRepository = listaRepository;
        this.cancionRepository = cancionRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
        this.usuarioCancionService = usuarioCancionService;
        this.listaMapper = listaMapper;
    }

    @Transactional(readOnly = true)
    public ListaResponseDto obtenerConRelaciones(Integer id) {
        Lista lista = listaRepository.findByIdConRelaciones(id);
        if (lista == null) throw new ListaNoEncontradaException("Lista no encontrada con ID: " + id);
        ListaResponseDto dto = listaMapper.toResponseDto(lista);
        dto.setCanciones(lista.getCanciones().stream().map(lc -> {
            var cancionDto = listaMapper.mapCancion(lc.getCancion());
            cancionDto.setFechaAgregada(lc.getFechaAgregada());
            return cancionDto;
        }).toList());
        return dto;
    }

    @Transactional(readOnly = true)
    public List<ListaResponseDto> obtenerListasPorUsuario(String nickname) {
        return listaRepository.findByUsuario_Nickname(nickname).stream().map(listaMapper::toResponseDto).toList();
    }

    public void agregarCancionALista(Integer listaId, Integer cancionId) {
        Lista lista = listaRepository.findByIdConRelaciones(listaId);
        if (lista == null) throw new ListaNoEncontradaException("Lista no encontrada " + listaId);
        Cancion cancion = cancionRepository.findById(cancionId)
                .orElseThrow(() -> new CancionNoEncontradaException("Canción no encontrada " + cancionId));
        boolean existe = lista.getCanciones().stream().anyMatch(lc -> lc.getCancion().getId().equals(cancionId));
        if (existe) throw new EntidadDuplicadaException("La canción ya está en la playlist.");
        ListaCancion lc = new ListaCancion();
        lc.setLista(lista);
        lc.setCancion(cancion);
        lc.setFechaAgregada(LocalDate.now());
        lista.getCanciones().add(lc);
        listaRepository.save(lista);
    }

    public void eliminarCancionDeLista(Integer listaId, Integer cancionId) {
        Lista lista = listaRepository.findByIdConRelaciones(listaId);
        if (lista == null) throw new ListaNoEncontradaException("Lista no encontrada.");
        lista.getCanciones().removeIf(lc -> lc.getCancion().getId().equals(cancionId));
        listaRepository.save(lista);
    }

    public void eliminar(Integer id) {
        if (!listaRepository.existsById(id))
            throw new ListaNoEncontradaException("No existe la lista con ID: " + id);
        listaRepository.deleteById(id);
    }

    @Override
    public void descargarPlaylistComoZip(Integer playlistId, String nickname, HttpServletResponse response) {
        Lista lista = listaRepository.findById(playlistId)
                .orElseThrow(() -> new ListaNoEncontradaException("Playlist no encontrada con ID: " + playlistId));

        if (lista.getCanciones() == null || lista.getCanciones().isEmpty()) {
            throw new RuntimeException("La playlist está vacía.");
        }

        try {
            String nombreZip = lista.getNombre()
                    .replaceAll("[^a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\\s\\-]", "")
                    .replaceAll("\\s+", "_") + ".zip";

            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + nombreZip + "\"");

            try (ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream())) {

                int cancionesAgregadas = 0;

                for (ListaCancion lc : lista.getCanciones()) {
                    Cancion cancion = lc.getCancion();

                    if (cancion == null || !cancion.isActivo()) continue;
                    if (cancion.getArtista() == null || !cancion.getArtista().isActivo()) continue;

                    Path rutaArchivo = Paths.get("src/main/resources/static/" + cancion.getAudio());
                    if (Files.exists(rutaArchivo)) {
                        String nombreArchivo = cancion.getTitulo()
                                .replaceAll("[^a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\\s\\-]", "")
                                .replaceAll("\\s+", "_") + ".mp3";

                        ZipEntry zipEntry = new ZipEntry(nombreArchivo);
                        zipOut.putNextEntry(zipEntry);
                        Files.copy(rutaArchivo, zipOut);
                        zipOut.closeEntry();

                        cancionesAgregadas++;
                    }
                }

                zipOut.finish();

                if (cancionesAgregadas == 0) {
                    throw new RuntimeException("No se encontraron canciones activas en la playlist.");
                }

            }

        } catch (IOException e) {
            throw new RuntimeException("Error al generar el archivo ZIP: " + e.getMessage(), e);
        }
    }


    public void crearPlaylistVacia(String nombre, Integer usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));
        Lista lista = new Lista();
        lista.setNombre(nombre);
        lista.setUsuario(usuario);
        lista.setFechaCreacion(LocalDate.now());
        listaRepository.save(lista);
    }

}
