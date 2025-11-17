package biblioteca.biblioteca.web.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResumenPrestamosDto {
    private final int totalPrestamos;
    private final int prestamosActivos;
    private final int prestamosVencidos;
    private final int prestamosDevueltos;
}