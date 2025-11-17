package biblioteca.biblioteca.application.query;

import biblioteca.biblioteca.web.dto.ResumenPrestamosDto;
import biblioteca.biblioteca.web.dto.PrestamoBibliotecarioDto;
import biblioteca.biblioteca.web.dto.AdminDashboardDto;
import java.time.LocalDate;
import java.util.List;

public interface IBibliotecaQueriesRepository {
    
    DashboardBibliotecarioCompleto obtenerDashboardCompleto(
        LocalDate fecha, 
        int diasProximoVencimiento, 
        int limitePrestamosDestacados
    );
    
    List<PrestamoBibliotecarioDto> obtenerTodosLosPrestamos(
            LocalDate fechaActual, int pagina, int tamanoPagina, String estadoFiltro);
    
    ResumenPrestamosDto obtenerResumenPrestamos(LocalDate fechaActual);

    // Dashboard administrador: métricas agregadas del sistema
    AdminDashboardDto obtenerResumenAdmin();
}