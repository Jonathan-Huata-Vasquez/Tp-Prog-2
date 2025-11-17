package biblioteca.biblioteca.domain.port;

import biblioteca.biblioteca.domain.model.Prestamo;
import lombok.Builder;
import lombok.Value;
import java.util.List;

/**
 * Pseudo-query que obtiene los 5 préstamos activos más antiguos
 * ordenados por fecha de vencimiento ascendente (los que vencen primero).
 */
@Value
@Builder
public class PrestamosDestacados {
    List<Prestamo> prestamos;  // Top 5 préstamos activos ordenados por fecha vencimiento ASC
}