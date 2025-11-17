package biblioteca.biblioteca.web.dto;

import lombok.Builder;
import lombok.Value;
import java.util.List;

/**
 * DTO que representa una página de resultados con información de paginación.
 * @param <T> Tipo de elementos en la página
 */
@Value
@Builder
public class PaginaDto<T> {
    List<T> contenido;
    int numeroPagina;      // 0-based
    int tamanoPagina;
    long totalElementos;
    int totalPaginas;
    boolean primera;
    boolean ultima;
    boolean tieneAnterior;
    boolean tieneSiguiente;
    
    public static <T> PaginaDto<T> of(List<T> contenido, int numeroPagina, int tamanoPagina, long totalElementos) {
        int totalPaginas = (int) Math.ceil((double) totalElementos / tamanoPagina);
        
        return PaginaDto.<T>builder()
                .contenido(contenido)
                .numeroPagina(numeroPagina)
                .tamanoPagina(tamanoPagina)
                .totalElementos(totalElementos)
                .totalPaginas(totalPaginas)
                .primera(numeroPagina == 0)
                .ultima(numeroPagina == totalPaginas - 1 || totalPaginas == 0)
                .tieneAnterior(numeroPagina > 0)
                .tieneSiguiente(numeroPagina < totalPaginas - 1)
                .build();
    }
}