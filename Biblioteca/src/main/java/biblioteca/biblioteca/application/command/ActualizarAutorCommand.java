package biblioteca.biblioteca.application.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class ActualizarAutorCommand {
    @NotNull Integer idAutor;
    @NotBlank String nombre;
    @NotNull LocalDate fechaNacimiento;
    @NotBlank String nacionalidad;
}
