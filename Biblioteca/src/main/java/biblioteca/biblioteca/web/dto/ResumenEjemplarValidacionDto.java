package biblioteca.biblioteca.web.dto;

import lombok.Builder;
import lombok.Data;

/**
 * DTO para mostrar el resumen de un ejemplar durante la validación de préstamo.
 */
@Data
@Builder
public class ResumenEjemplarValidacionDto {
    private Integer idEjemplar;
    private String tituloLibro;
    private String autor;
    private Boolean disponible;
    private String estadoDetalle;
}