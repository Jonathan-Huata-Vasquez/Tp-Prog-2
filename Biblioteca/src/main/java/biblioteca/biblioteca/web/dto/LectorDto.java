package biblioteca.biblioteca.web.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class LectorDto {
    Integer id;
    String nombre;
    LocalDate bloqueadoHasta;   // null si no está bloqueado
    Integer prestamosActivos;   // opcional: conteo (no lista)
}
