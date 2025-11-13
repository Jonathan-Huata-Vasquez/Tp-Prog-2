package biblioteca.biblioteca.application.query;

import lombok.Builder;
import lombok.Value;

import lombok.Builder;
import lombok.Value;

/** Si idLibro != null, filtra por ese libro; si es null, trae todas. */
@Value
@Builder
public class ListarCopiasQuery {
    Integer idLibro;
}