package biblioteca.biblioteca.web.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class LibroCatalogoItemDto {
    Integer id;
    String  titulo;
    Integer anioPublicacion;
    String  categoria;

    Integer idAutor;
    String  autorNombre;       // lookup Autor
    Integer idEditorial;
    String  editorialNombre;   // lookup Editorial

    String  descripcion;      // descripción completa
    String  descripcionCorta; // derivada de Libro.descripcion (ej. primeros N chars)
    String  portadaUrl;        // OPCIONAL: si null → la vista usa placeholder por defecto

    Integer totalCopias;
    Integer copiasDisponibles;

    public boolean isDisponible() {
        return copiasDisponibles != null && copiasDisponibles > 0;
    }
}
