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
    String fechaPrestamo;    // Formateada para mostrar
    String fechaVencimiento; // Formateada para mostrar
    String estado;           // Mapeado del enum: "ACTIVO", "VENCIDO", "DEVUELTO"
}