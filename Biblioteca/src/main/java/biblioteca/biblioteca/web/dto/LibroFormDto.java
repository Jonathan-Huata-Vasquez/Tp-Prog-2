package biblioteca.biblioteca.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LibroFormDto {
    private Integer id; // solo para edición

    @NotBlank
    private String titulo;

    @NotNull
    private Integer idAutor;

    @NotNull
    private Integer idEditorial;

    @NotBlank
    private String categoria;

    private Integer anioPublicacion;
    private String descripcion;
}
