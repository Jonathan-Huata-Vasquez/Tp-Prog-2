package biblioteca.biblioteca.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@Builder
@RequiredArgsConstructor
public class LectorBibliotecarioDto {
    private final Integer idLector;
    private final String nombreCompleto;
    private final boolean bloqueado;
    private final int prestamosActivos;
    private final int prestamosVencidos;
    private final int totalDevueltos;
    private final int diasBloqueo;
}