package biblioteca.biblioteca.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * DTO resultado para detalle completo de lector.
 */
@Getter
@Builder
@RequiredArgsConstructor
public class DetalleLectorResultDto {
    private final LectorDetalleDto lector;
    private final ResumenLectorDto resumen;
    private final List<PrestamoLectorDto> prestamosActivos;
    private final List<PrestamoLectorDto> prestamosDevueltos;
}