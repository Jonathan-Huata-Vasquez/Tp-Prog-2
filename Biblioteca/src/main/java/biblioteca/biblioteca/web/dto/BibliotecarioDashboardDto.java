package biblioteca.biblioteca.web.dto;

import lombok.Builder;
import lombok.Value;
import java.util.List;

@Value
@Builder
public class BibliotecarioDashboardDto {
    // Estadísticas de resumen
    Integer cantidadActivos;
    Integer cantidadVencidos;
    Integer cantidadHoy;
    Integer proximosVencimientos;
    
    // Préstamos destacados (vencidos + próximos a vencer)
    List<PrestamoDetalleDto> prestamosDestacados;
}