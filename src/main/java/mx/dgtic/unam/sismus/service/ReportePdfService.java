package mx.dgtic.unam.sismus.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import mx.dgtic.unam.sismus.repository.*;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class ReportePdfService implements ReporteService {

    private final CancionRepository cancionRepository;
    private final ListaRepository listaRepository;
    private final UsuarioRepository usuarioRepository;

    public ReportePdfService(CancionRepository cancionRepository,
                             ListaRepository listaRepository,
                             UsuarioRepository usuarioRepository) {
        this.cancionRepository = cancionRepository;
        this.listaRepository = listaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public ByteArrayInputStream generarReportePdf(String tipo) {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, out);
            document.open();
            document.addTitle("Reporte: " + tipo);
            document.add(new Paragraph("Sismus - Reporte de " + tipo.toUpperCase()));
            document.add(new Paragraph(" "));
            PdfPTable table = switch (tipo) {
                case "canciones" -> crearTablaCanciones();
                case "listas" -> crearTablaListas();
                case "actividad" -> crearTablaActividadUsuarios();
                default -> new PdfPTable(1);
            };
            document.add(table);
            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Error generando PDF", e);
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    private PdfPTable crearTablaCanciones() {
        var canciones = cancionRepository.findAll();
        PdfPTable table = new PdfPTable(4);
        table.addCell("Título");
        table.addCell("Artista");
        table.addCell("Género");
        table.addCell("Activo");
        for (var c : canciones) {
            table.addCell(c.getTitulo());
            table.addCell(c.getArtista().getNombre());
            table.addCell(c.getGenero().getNombre());
            table.addCell(c.isActivo() ? "Sí" : "No");
        }
        return table;
    }

    private PdfPTable crearTablaListas() {
        var listas = listaRepository.findAllConRelaciones();
        PdfPTable table = new PdfPTable(3);
        table.addCell("Lista");
        table.addCell("Usuario");
        table.addCell("Total Canciones");
        listas.forEach(l -> {
            table.addCell(l.getNombre());
            table.addCell(l.getUsuario().getNickname());
            table.addCell(String.valueOf(l.getCanciones().size()));
        });
        return table;
    }

    private PdfPTable crearTablaActividadUsuarios() {
        var datos = usuarioRepository.reporteActividadUsuarios();
        PdfPTable table = new PdfPTable(4);
        table.addCell("Usuario");
        table.addCell("Descargas");
        table.addCell("Listas Creadas");
        table.addCell("Último Acceso");
        for (Object[] fila : datos) {
            table.addCell((String) fila[0]);
            table.addCell(String.valueOf(fila[1]));
            table.addCell(String.valueOf(fila[2]));
            table.addCell(fila[3] != null ? fila[3].toString() : "—");
        }
        return table;
    }

    public ByteArrayInputStream generarReporteExcel(String tipo) {
        throw new UnsupportedOperationException("Usar ReporteExcelService para Excel");
    }
}
