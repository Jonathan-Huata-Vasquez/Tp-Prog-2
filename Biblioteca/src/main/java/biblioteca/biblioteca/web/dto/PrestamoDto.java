package biblioteca.biblioteca.web.dto;

import lombok.Builder;
import lombok.Value;
import java.time.LocalDate;

@Value
@Builder
public class PrestamoDto {
    Integer id;
    Integer idLector;
    Integer idCopia;
    LocalDate fechaInicio;
    LocalDate fechaVencimiento;
    LocalDate fechaDevolucion; // null si sigue abierto
}
