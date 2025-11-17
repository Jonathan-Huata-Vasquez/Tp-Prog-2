package biblioteca.biblioteca.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * DTO para resumen de préstamos de un lector específico.
 */
@Getter
@Builder
@RequiredArgsConstructor
public class ResumenLectorDto {
    private final int prestamosActivos;
    private final int prestamosVencidos;
    private final int totalDevueltos;
}