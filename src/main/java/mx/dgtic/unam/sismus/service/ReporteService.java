package mx.dgtic.unam.sismus.service;

import java.io.ByteArrayInputStream;

public interface ReporteService {
    ByteArrayInputStream generarReportePdf(String tipo);
    ByteArrayInputStream generarReporteExcel(String tipo);
}
