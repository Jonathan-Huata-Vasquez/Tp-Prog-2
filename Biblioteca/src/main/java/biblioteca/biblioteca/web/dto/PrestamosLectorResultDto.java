package biblioteca.biblioteca.web.dto;

import lombok.Builder;
import lombok.Value;
import java.util.List;

@Value
@Builder
public class PrestamosLectorResultDto {
    ResumenPrestamosDto resumen;
    List<PrestamoLectorDto> prestamos;
    
    @Value
    @Builder
    public static class ResumenPrestamosDto {
        Integer cantidadActivos;
        Integer cantidadVencidos;
        Integer cantidadDevueltos;
    }
}