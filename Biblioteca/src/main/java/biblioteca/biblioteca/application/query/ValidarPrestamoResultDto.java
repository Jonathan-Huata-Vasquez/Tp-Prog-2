package biblioteca.biblioteca.application.query;

import biblioteca.biblioteca.web.dto.ResumenLectorValidacionDto;
import biblioteca.biblioteca.web.dto.ResumenEjemplarValidacionDto;
import lombok.Builder;
import lombok.Value;

/**
 * Resultado de la validación de préstamo.
 */
@Value
@Builder
public class ValidarPrestamoResultDto {
    boolean lectorValido;
    boolean copiaValida;
    boolean puedeRegistrar;
    String mensajeError;
    ResumenLectorValidacionDto resumenLector;
    ResumenEjemplarValidacionDto resumenEjemplar;
}