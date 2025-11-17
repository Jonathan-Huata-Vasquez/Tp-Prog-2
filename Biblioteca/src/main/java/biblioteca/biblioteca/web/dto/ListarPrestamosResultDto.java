package biblioteca.biblioteca.web.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ListarPrestamosResultDto {
    PaginaDto<PrestamoBibliotecarioDto> paginaPrestamos;
    ResumenPrestamosDto resumen;
}