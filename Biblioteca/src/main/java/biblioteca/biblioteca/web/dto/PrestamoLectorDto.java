package biblioteca.biblioteca.web.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PrestamoLectorDto {
    Integer idPrestamo;
    String tituloLibro;
    String autorNombre;
    Integer idEjemplar;
    java.time.LocalDate fechaPrestamo;    // LocalDate para cálculos y formato
    java.time.LocalDate fechaVencimiento; // LocalDate para cálculos y formato
    java.time.LocalDate fechaDevolucion;  // LocalDate, null si no devuelto
    String estado;           // Mapeado del enum: "ACTIVO", "VENCIDO", "DEVUELTO"
    Integer diasAtraso;      // Solo para préstamos vencidos
}