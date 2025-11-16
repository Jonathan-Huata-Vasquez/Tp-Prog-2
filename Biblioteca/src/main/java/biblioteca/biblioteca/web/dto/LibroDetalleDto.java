package biblioteca.biblioteca.web.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class LibroDetalleDto {
    Integer id;
    String titulo;
    String autorNombre;
    String editorialNombre;
    String descripcion;
    String categoria;
    Integer anioPublicacion;

    Integer totalCopias;
    Integer copiasDisponibles;
    Boolean prestadoAlLector;

    List<CopiaDetalleDto> copias;
}
