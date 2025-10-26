package mx.dgtic.unam.sismus.service;

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

    public ReporteExcelService(CancionRepository cancionRepository,
                               ListaRepository listaRepository,
                               UsuarioRepository usuarioRepository) {
        this.cancionRepository = cancionRepository;
        this.listaRepository = listaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public ByteArrayInputStream generarReporteExcel(String tipo) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Reporte " + tipo);
            switch (tipo) {
                case "canciones" -> exportarCanciones(sheet);
                case "listas" -> exportarListas(sheet);
                case "actividad" -> exportarActividad(sheet);
                default -> throw new IllegalArgumentException("Tipo de reporte inválido: " + tipo);
            }
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Error generando Excel", e);
        }
    }

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

    public ByteArrayInputStream generarReportePdf(String tipo) {
        throw new UnsupportedOperationException("Usar ReportePdfService para PDF");
    }
}
