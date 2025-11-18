package biblioteca.biblioteca.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AutorFormDto {
    private Integer id;
    private String nombre;
    private java.time.LocalDate fechaNacimiento;
    private String nacionalidad;
}
