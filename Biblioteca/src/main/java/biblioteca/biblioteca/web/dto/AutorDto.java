package biblioteca.biblioteca.web.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
@Value
@Builder
public class AutorDto {
    Integer id;
    String nombre;
    LocalDate fechaNacimiento;
    String nacionalidad;
}
