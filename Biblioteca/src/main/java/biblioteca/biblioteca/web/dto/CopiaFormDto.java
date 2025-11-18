package biblioteca.biblioteca.web.dto;

import biblioteca.biblioteca.domain.model.EstadoCopia;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CopiaFormDto {
    private Integer id;
    private Integer idLibro;
    private EstadoCopia estado;
}
