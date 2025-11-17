package biblioteca.biblioteca.application.query;

import biblioteca.biblioteca.web.dto.PrestamoBibliotecarioDto;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ValidarDevolucionResultDto {
    boolean puedeDevolver;
    PrestamoBibliotecarioDto resumenPrestamo;
    String mensajeError;
}
