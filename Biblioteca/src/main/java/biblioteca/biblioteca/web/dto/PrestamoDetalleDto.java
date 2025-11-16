package biblioteca.biblioteca.web.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PrestamoDetalleDto {
    // Información básica del préstamo
    String codigo;               // "P-{idPrestamo}"
    String estado;               // "ACTIVO", "VENCIDO", "DEVUELTO"
    Integer diasAtraso;
    
    // Fechas
    String fechaPrestamo;
    String fechaVencimiento;
    String fechaDevolucion;      // null si no devuelto
    
    // Información del libro
    String tituloLibro;
    String autor;
    String editorial;
    String anioPublicacion;
    String urlPortada;           // null por ahora (futuro)
    
    // Información del lector
    Integer idLector;
    String nombreLector;
}