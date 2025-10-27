package mx.dgtic.unam.sismus.service;

import mx.dgtic.unam.sismus.model.Artista;
import mx.dgtic.unam.sismus.model.Genero;
import mx.dgtic.unam.sismus.repository.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class ReporteExcelService implements ReporteService {

    private final CancionRepository cancionRepository;
    private final ListaRepository listaRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioCancionRepository usuarioCancionRepository;

    public ReporteExcelService(CancionRepository cancionRepository,
                               ListaRepository listaRepository,
                               UsuarioRepository usuarioRepository,
                               UsuarioCancionRepository usuarioCancionRepository) {
        this.cancionRepository = cancionRepository;
        this.listaRepository = listaRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioCancionRepository = usuarioCancionRepository;
    }

    @Override
    public ByteArrayInputStream generarReporteExcel(String tipo) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Reporte " + tipo);

            switch (tipo) {
                case "canciones" -> exportarCanciones(sheet);
                case "listas" -> exportarListas(sheet);
                case "actividad" -> exportarActividad(sheet);
                case "artistas" -> exportarTopArtistas(sheet);
                case "generos" -> exportarTopGeneros(sheet);
                default -> throw new IllegalArgumentException("Tipo de reporte inválido: " + tipo);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Error generando Excel", e);
        }
    }

    // Reporte: Canciones
    private void exportarCanciones(Sheet sheet) {
        var canciones = cancionRepository.findAll();
        String[] headers = {"Título", "Artista", "Género", "Activo"};
        Row header = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) header.createCell(i).setCellValue(headers[i]);

        int rowNum = 1;
        for (var c : canciones) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(c.getTitulo());
            row.createCell(1).setCellValue(c.getArtista().getNombre());
            row.createCell(2).setCellValue(c.getGenero().getNombre());
            row.createCell(3).setCellValue(c.isActivo() ? "Sí" : "No");
        }
    }

    // Reporte: Listas
    private void exportarListas(Sheet sheet) {
        var listas = listaRepository.findAllConRelaciones();
        String[] headers = {"Lista", "Usuario", "Total Canciones"};
        Row header = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) header.createCell(i).setCellValue(headers[i]);

        int rowNum = 1;
        for (var l : listas) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(l.getNombre());
            row.createCell(1).setCellValue(l.getUsuario().getNickname());
            row.createCell(2).setCellValue(l.getCanciones().size());
        }
    }

    // Reporte: Actividad de usuarios
    private void exportarActividad(Sheet sheet) {
        var datos = usuarioRepository.reporteActividadUsuarios();
        String[] headers = {"Usuario", "Total Descargas", "Total Listas", "Último Acceso"};
        Row header = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) header.createCell(i).setCellValue(headers[i]);

        int rowNum = 1;
        for (Object[] fila : datos) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue((String) fila[0]);
            row.createCell(1).setCellValue(String.valueOf(fila[1]));
            row.createCell(2).setCellValue(String.valueOf(fila[2]));
            row.createCell(3).setCellValue(fila[3] != null ? fila[3].toString() : "—");
        }
    }

    // Reporte: Top Artistas más descargados
    private void exportarTopArtistas(Sheet sheet) {
        var datos = usuarioCancionRepository.topArtistasMasDescargados();
        String[] headers = {"Artista", "Total Descargas"};
        Row header = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) header.createCell(i).setCellValue(headers[i]);

        int rowNum = 1;
        for (Object[] fila : datos) {
            Row row = sheet.createRow(rowNum++);
            Artista artista = (Artista) fila[0];
            row.createCell(0).setCellValue(artista.getNombre());
            row.createCell(1).setCellValue(String.valueOf(fila[1]));
        }
    }

    // Reporte: Top Géneros más descargados
    private void exportarTopGeneros(Sheet sheet) {
        var datos = usuarioCancionRepository.topGenerosMasDescargados();
        String[] headers = {"Género", "Total Descargas"};
        Row header = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) header.createCell(i).setCellValue(headers[i]);

        int rowNum = 1;
        for (Object[] fila : datos) {
            Row row = sheet.createRow(rowNum++);
            Genero genero = (Genero) fila[0];
            row.createCell(0).setCellValue(genero.getNombre());
            row.createCell(1).setCellValue(String.valueOf(fila[1]));
        }
    }

    @Override
    public ByteArrayInputStream generarReportePdf(String tipo) {
        throw new UnsupportedOperationException("Usar ReportePdfService para PDF");
    }
}
