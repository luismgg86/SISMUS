package mx.dgtic.unam.sismus.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import mx.dgtic.unam.sismus.model.Artista;
import mx.dgtic.unam.sismus.model.Genero;
import mx.dgtic.unam.sismus.repository.*;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class ReportePdfService implements ReporteService {

    private final CancionRepository cancionRepository;
    private final ListaRepository listaRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioCancionRepository usuarioCancionRepository;

    public ReportePdfService(CancionRepository cancionRepository,
                             ListaRepository listaRepository,
                             UsuarioRepository usuarioRepository,
                             UsuarioCancionRepository usuarioCancionRepository) {
        this.cancionRepository = cancionRepository;
        this.listaRepository = listaRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioCancionRepository = usuarioCancionRepository;
    }

    @Override
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
                case "artistas" -> crearTablaTopArtistas();
                case "generos" -> crearTablaTopGeneros();
                default -> new PdfPTable(1);
            };

            document.add(table);
            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Error generando PDF", e);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    // Reporte: Canciones
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

    // Reporte: Listas
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

    // Reporte: Actividad de usuarios
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

    // Reporte: Top Artistas mas descargados
    private PdfPTable crearTablaTopArtistas() {
        var datos = usuarioCancionRepository.topArtistasMasDescargados();
        PdfPTable table = new PdfPTable(2);
        table.addCell("Artista");
        table.addCell("Total Descargas");

        for (Object[] fila : datos) {
            Artista artista = (Artista) fila[0];
            table.addCell(artista.getNombre());
            table.addCell(String.valueOf(fila[1]));
        }
        return table;
    }

    // Reporte: Top Generos mas descargados
    private PdfPTable crearTablaTopGeneros() {
        var datos = usuarioCancionRepository.topGenerosMasDescargados();
        PdfPTable table = new PdfPTable(2);
        table.addCell("Género");
        table.addCell("Total Descargas");

        for (Object[] fila : datos) {
            Genero genero = (Genero) fila[0];
            table.addCell(genero.getNombre());
            table.addCell(String.valueOf(fila[1]));
        }
        return table;
    }

    @Override
    public ByteArrayInputStream generarReporteExcel(String tipo) {
        throw new UnsupportedOperationException("Usar ReporteExcelService para Excel");
    }
}
