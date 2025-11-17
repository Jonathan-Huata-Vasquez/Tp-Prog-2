package biblioteca.biblioteca.application.query;

import biblioteca.biblioteca.web.dto.BibliotecarioDashboardDto;
import lombok.Builder;
import lombok.Value;

/**
 * Resultado de pseudo-query unificada que obtiene todos los datos del dashboard del bibliotecario
 * en una sola consulta optimizada y retorna directamente el DTO completo.
 * Este objeto pertenece a la capa de aplicación, no al dominio.
 */
@Value
@Builder
public class DashboardBibliotecarioCompleto {
    BibliotecarioDashboardDto dashboard;
}