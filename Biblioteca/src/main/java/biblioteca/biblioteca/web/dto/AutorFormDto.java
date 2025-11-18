package biblioteca.biblioteca.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AutorFormDto {
    private Integer id;
    private String nombre;
    private LocalDate fechaNacimiento;
    private String nacionalidad;
}
