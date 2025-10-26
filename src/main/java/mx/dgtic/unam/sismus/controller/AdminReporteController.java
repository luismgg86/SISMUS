package mx.dgtic.unam.sismus.controller;

import mx.dgtic.unam.sismus.service.ReportePdfService;
import mx.dgtic.unam.sismus.service.ReporteExcelService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/reportes")
public class AdminReporteController {

    private final ReportePdfService pdfService;
    private final ReporteExcelService excelService;

    public AdminReporteController(ReportePdfService pdfService, ReporteExcelService excelService) {
        this.pdfService = pdfService;
        this.excelService = excelService;
    }

    @GetMapping
    public String mostrarReportes(Model model) {
        model.addAttribute("titulo", "Reportes del Sistema");
        model.addAttribute("contenido", "admin/reportes");
        return "layout/main";
    }

    @GetMapping("/pdf/{tipo}")
    @ResponseBody
    public ResponseEntity<InputStreamResource> descargarPdf(@PathVariable String tipo) {
        var bis = pdfService.generarReportePdf(tipo);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=reporte_" + tipo + ".pdf");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    @GetMapping("/excel/{tipo}")
    @ResponseBody
    public ResponseEntity<InputStreamResource> descargarExcel(@PathVariable String tipo) {
        var bis = excelService.generarReporteExcel(tipo);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=reporte_" + tipo + ".xlsx");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(bis));
    }
}
