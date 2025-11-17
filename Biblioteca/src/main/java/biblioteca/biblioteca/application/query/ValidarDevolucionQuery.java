package biblioteca.biblioteca.application.query;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ValidarDevolucionQuery {
    Integer idLector;
    Integer idCopia;
}
