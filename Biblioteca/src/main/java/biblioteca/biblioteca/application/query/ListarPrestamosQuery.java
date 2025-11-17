package biblioteca.biblioteca.application.query;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ListarPrestamosQuery {
    // Parámetros de paginación
    @Builder.Default
    Integer pagina = 0;          // 0-based
    
    @Builder.Default  
    Integer tamanoPagina = 20;   // elementos por página
    
    // Futuros filtros (extensible)
    String estadoFiltro;         // "ACTIVO", "VENCIDO", "DEVUELTO", null = todos
    Integer idLector;            // filtrar por lector específico
}