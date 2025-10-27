package mx.dgtic.unam.sismus.service;

import mx.dgtic.unam.sismus.dto.ArtistaDto;
import mx.dgtic.unam.sismus.dto.CancionResponseDto;
import mx.dgtic.unam.sismus.dto.GeneroDto;
import mx.dgtic.unam.sismus.dto.UsuarioCancionDto;

import java.util.List;

public interface UsuarioCancionService {
    UsuarioCancionDto registrarDescarga(Integer usuarioId, Integer cancionId);
    List<CancionResponseDto> obtenerTop10CancionesMasDescargadas();
    List<ArtistaDto> obtenerTopArtistasMasDescargados();
    List<GeneroDto> obtenerTopGenerosMasDescargados();
}
