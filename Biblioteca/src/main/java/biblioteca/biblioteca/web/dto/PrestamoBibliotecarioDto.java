package biblioteca.biblioteca.web.dto;

import lombok.Builder;
import lombok.Value;
import java.time.LocalDate;

/**
 * DTO para mostrar préstamos en la vista de bibliotecario.
 * Incluye información completa del préstamo, lector y libro.
 */
@Value
@Builder
public class PrestamoBibliotecarioDto {
    // Datos del préstamo
    Integer idPrestamo;
    Integer idCopia;
    LocalDate fechaInicio;
    LocalDate fechaVencimiento;
    LocalDate fechaDevolucion; 
    String estado;
    Integer diasAtraso;
    
    // Datos del lector
    Integer idLector;
    String nombreLector;
    Boolean lectorBloqueado;
    
    // Datos del libro
    String tituloLibro;
    String autorNombre;
    String editorialNombre;
}