package biblioteca.biblioteca.application.query;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Query para listar lectores con filtros opcionales.
 */
@Getter
@Builder
@RequiredArgsConstructor
public class ListarLectoresQuery {
    private final String estadoFiltro; // "HABILITADO", "BLOQUEADO" o null
    private final int pagina;
    private final int tamano;
}