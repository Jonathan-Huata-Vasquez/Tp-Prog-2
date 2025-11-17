package biblioteca.biblioteca.application.query;

import lombok.Builder;
import lombok.Value;

/**
 * Query para validar datos de un préstamo antes de registrarlo.
 */
@Value
@Builder
public class ValidarPrestamoQuery {
    Integer idLector;
    Integer idCopia;
}