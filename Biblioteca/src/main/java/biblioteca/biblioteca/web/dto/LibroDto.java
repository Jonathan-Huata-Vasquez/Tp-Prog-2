package biblioteca.biblioteca.web.dto;

import biblioteca.biblioteca.domain.model.Categoria;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class LibroDto {
    Integer id;
    String titulo;
    Integer anioPublicacion;
    Integer idAutor;
    Integer idEditorial;
    Categoria categoria;
}
