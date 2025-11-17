package biblioteca.biblioteca.application.query;

import java.time.LocalDate;

/**
 * Repositorio específico para queries complejas del dashboard de biblioteca.
 * Separa las consultas específicas de la aplicación del repositorio de dominio.
 */
public interface IBibliotecaQueriesRepository {
    
    /**
     * Obtiene todos los datos del dashboard del bibliotecario en una sola consulta optimizada.
     * Incluye estadísticas y préstamos destacados, retornando directamente el DTO completo.
     * 
     * @param fecha Fecha actual para calcular vencimientos
     * @param diasProximoVencimiento Días para considerar "próximo a vencer"
     * @param limitePrestamosDestacados Número máximo de préstamos destacados
     * @return Dashboard completo con todas las estadísticas y préstamos
     */
    DashboardBibliotecarioCompleto obtenerDashboardCompleto(
        LocalDate fecha, 
        int diasProximoVencimiento, 
        int limitePrestamosDestacados
    );
}