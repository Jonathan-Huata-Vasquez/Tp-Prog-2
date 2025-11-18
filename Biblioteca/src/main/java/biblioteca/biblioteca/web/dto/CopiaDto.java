package biblioteca.biblioteca.web.dto;

import biblioteca.biblioteca.domain.model.EstadoCopia;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CopiaDto {
    Integer id;
    Integer idLibro;
    String tituloLibro;
    EstadoCopia estado;
}
