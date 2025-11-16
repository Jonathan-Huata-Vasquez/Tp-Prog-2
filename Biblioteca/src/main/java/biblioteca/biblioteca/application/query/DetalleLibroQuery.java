package biblioteca.biblioteca.application.query;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DetalleLibroQuery {
    Integer idLibro;
    Integer idLectorActual; // opcional para saber si lo tiene prestado
}
