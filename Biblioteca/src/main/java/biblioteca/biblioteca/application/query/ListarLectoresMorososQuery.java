package biblioteca.biblioteca.application.query;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class ListarLectoresMorososQuery {
    @NotNull LocalDate hastaFecha;
}
