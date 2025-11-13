package biblioteca.biblioteca.application.command;

import biblioteca.biblioteca.domain.model.Categoria;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CrearLibroCommand {
    @NotBlank String titulo;
    @NotNull Integer anioPublicacion;
    @NotNull Integer idAutor;
    @NotNull Integer idEditorial;
    @NotNull Categoria categoria;
}
