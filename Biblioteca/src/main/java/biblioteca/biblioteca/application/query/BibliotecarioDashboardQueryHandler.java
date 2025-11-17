package biblioteca.biblioteca.application.query;

import biblioteca.biblioteca.web.dto.BibliotecarioDashboardDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class BibliotecarioDashboardQueryHandler {

    private final IBibliotecaQueriesRepository bibliotecaQueries;
    
    // Configuración interna del handler
    private static final int LIMITE_PRESTAMOS_DESTACADOS = 5;
    private static final int DIAS_PROXIMO_VENCIMIENTO = 3;

    public BibliotecarioDashboardDto handle(BibliotecarioDashboardQuery query) {
        LocalDate hoy = LocalDate.now();
        
        log.debug("Procesando dashboard del bibliotecario para fecha: {}", hoy);

        // PSEUDO-QUERY UNIFICADA: Obtener todo el dashboard en una sola consulta optimizada
        DashboardBibliotecarioCompleto dashboardCompleto = bibliotecaQueries.obtenerDashboardCompleto(
            hoy, 
            DIAS_PROXIMO_VENCIMIENTO, 
            LIMITE_PRESTAMOS_DESTACADOS
        );
        
        BibliotecarioDashboardDto dashboard = dashboardCompleto.getDashboard();
        
        log.debug("Dashboard procesado: {} activos, {} vencidos, {} destacados", 
                  dashboard.getCantidadActivos(), 
                  dashboard.getCantidadVencidos(), 
                  dashboard.getPrestamosDestacados().size());

        return dashboard;
    }
}