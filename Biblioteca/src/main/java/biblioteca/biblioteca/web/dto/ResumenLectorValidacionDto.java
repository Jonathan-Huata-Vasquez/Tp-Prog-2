package biblioteca.biblioteca.web.dto;

import lombok.Builder;
import lombok.Data;

/**
 * DTO para mostrar el resumen de un lector durante la validación de préstamo.
 */
@Data
@Builder
public class ResumenLectorValidacionDto {
    private Integer id;
    private String nombre;
    private Integer prestamosActivos;
    private Boolean bloqueado;
    private String motivoBloqueo;
}