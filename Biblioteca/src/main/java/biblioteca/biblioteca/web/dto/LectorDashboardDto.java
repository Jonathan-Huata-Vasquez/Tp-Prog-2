package biblioteca.biblioteca.web.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class LectorDashboardDto {
    // Estado de bloqueo
    boolean bloqueado;
    String bloqueadoHasta;      // Fecha formateada, null si no bloqueado
    
    // Métricas de préstamos
    int prestamosActivos;
    String proximoVencimiento;  // Fecha formateada del préstamo más próximo a vencer, null si no hay
}