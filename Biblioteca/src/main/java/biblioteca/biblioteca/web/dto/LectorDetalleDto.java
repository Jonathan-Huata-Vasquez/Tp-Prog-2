package biblioteca.biblioteca.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para mostrar información detallada de un lector.
 */
@Getter
@Builder
@RequiredArgsConstructor
public class LectorDetalleDto {
    private final Integer idLector;
    private final String nombreCompleto;
    private final boolean bloqueado;
    private final LocalDate bloqueadoHasta;
    private final int diasBloqueo;
}