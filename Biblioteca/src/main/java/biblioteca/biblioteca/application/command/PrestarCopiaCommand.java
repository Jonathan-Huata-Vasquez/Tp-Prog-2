package biblioteca.biblioteca.application.command;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

/** Intención de prestar una copia a un lector por un bibliotecario. */
@Value
@Builder
public class PrestarCopiaCommand {
    Integer idUsuario;          // opcional para autorización
    @NotNull Integer idLector;  // validación de request
    @NotNull Integer idCopia;
}
