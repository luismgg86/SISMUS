package mx.dgtic.unam.sismus.service;

import jakarta.transaction.Transactional;
import mx.dgtic.unam.sismus.exception.CancionDuplicadaException;
import mx.dgtic.unam.sismus.exception.CancionNoEncontradaException;
import mx.dgtic.unam.sismus.exception.ListaNoEncontradaException;
import mx.dgtic.unam.sismus.model.Cancion;
import mx.dgtic.unam.sismus.model.Lista;
import mx.dgtic.unam.sismus.model.ListaCancion;
import mx.dgtic.unam.sismus.repository.CancionRepository;
import mx.dgtic.unam.sismus.repository.ListaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ListaServiceImpl implements ListaService {

    private final ListaRepository listaRepository;
    private final CancionRepository cancionRepository;

    public ListaServiceImpl(ListaRepository listaRepository, CancionRepository cancionRepository) {
        this.listaRepository = listaRepository;
        this.cancionRepository = cancionRepository;
    }

    /* ========================
       CRUD BÁSICO
     ======================== */

    @Override
    public List<Lista> listarTodas() {
        return listaRepository.findAllConRelaciones();
    }

    @Override
    public Optional<Lista> buscarPorId(Integer id) {
        return listaRepository.findById(id);
    }

    @Override
    public Lista guardar(Lista lista) {
        return listaRepository.save(lista);
    }

    @Override
    public void eliminar(Integer id) {
        if (!listaRepository.existsById(id)) {
            throw new ListaNoEncontradaException("La lista con ID " + id + " no existe.");
        }
        listaRepository.deleteById(id);
    }

    /* ========================
       FUNCIONALIDAD DE NEGOCIO
     ======================== */

    @Override
    public Lista obtenerConRelaciones(Integer id) {
        Lista lista = listaRepository.findByIdConRelaciones(id);
        if (lista == null) {
            throw new ListaNoEncontradaException("No se encontró la lista con ID: " + id);
        }
        return lista;
    }

    @Override
    public List<Lista> obtenerListasPorUsuario(String nickname) {
        return listaRepository.findByUsuario_Nickname(nickname);
    }

    @Override
    public Lista crearPlaylist(String nombre) {
        Lista nuevaLista = new Lista();
        nuevaLista.setNombre(nombre);
        nuevaLista.setFechaCreacion(LocalDate.now());
        // Futuro: asignar usuario autenticado
        return listaRepository.save(nuevaLista);
    }

    /* ========================
       AGREGAR / ELIMINAR CANCIONES
     ======================== */

    @Override
    public void agregarCancionALista(Integer listaId, Integer cancionId) {
        Lista lista = listaRepository.findByIdConRelaciones(listaId);
        if (lista == null) {
            throw new ListaNoEncontradaException("Lista no encontrada con ID: " + listaId);
        }

        Cancion cancion = cancionRepository.findById(cancionId)
                .orElseThrow(() -> new CancionNoEncontradaException("Canción no encontrada con ID: " + cancionId));

        // Evitar duplicados
        boolean yaExiste = lista.getCanciones().stream()
                .anyMatch(lc -> lc.getCancion().getId().equals(cancionId));

        if (yaExiste) {
            throw new CancionDuplicadaException("La canción ya está en la playlist");
        }

        ListaCancion listaCancion = new ListaCancion();
        listaCancion.setLista(lista);
        listaCancion.setCancion(cancion);
        listaCancion.setFechaAgregada(LocalDate.now());

        lista.getCanciones().add(listaCancion);
        listaRepository.save(lista);
    }

    @Override
    public void eliminarCancionDeLista(Integer listaId, Integer cancionId) {
        Lista lista = listaRepository.findByIdConRelaciones(listaId);
        if (lista == null) {
            throw new ListaNoEncontradaException("Lista no encontrada con ID: " + listaId);
        }

        lista.getCanciones().removeIf(lc -> lc.getCancion().getId().equals(cancionId));
        listaRepository.save(lista);
    }
}
