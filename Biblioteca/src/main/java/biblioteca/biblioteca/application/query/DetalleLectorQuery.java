package biblioteca.biblioteca.application.query;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Query para obtener detalle completo de un lector.
 */
@Getter
@Builder
@RequiredArgsConstructor
public class DetalleLectorQuery {
    private final Integer idLector;
}