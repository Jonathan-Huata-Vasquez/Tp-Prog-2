package biblioteca.biblioteca.application.query;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class BibliotecarioDashboardQuery {
    // Sin parámetros por ahora - el handler maneja los límites internamente
    // Futuro: se pueden agregar filtros específicos si es necesario
}