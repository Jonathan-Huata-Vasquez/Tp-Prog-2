package biblioteca.biblioteca.application.query;

import biblioteca.biblioteca.web.dto.ListarPrestamosResultDto;
import biblioteca.biblioteca.web.dto.PaginaDto;
import biblioteca.biblioteca.web.dto.PrestamoBibliotecarioDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ListarPrestamosQueryHandler {

    private final IBibliotecaQueriesRepository bibliotecaQueries;

    @Transactional(readOnly = true)
    public ListarPrestamosResultDto handle(ListarPrestamosQuery query) {
        LocalDate fechaActual = LocalDate.now();
        
        var resumen = bibliotecaQueries.obtenerResumenPrestamos(fechaActual);
        List<PrestamoBibliotecarioDto> prestamos = bibliotecaQueries.obtenerTodosLosPrestamos(
            fechaActual, 
            query.getPagina(), 
            query.getTamanoPagina(),
            query.getEstadoFiltro()
        );
        
        int totalElementos = resumen.getTotalPrestamos();
        PaginaDto<PrestamoBibliotecarioDto> pagina = PaginaDto.of(
            prestamos, 
            query.getPagina(), 
            query.getTamanoPagina(), 
            totalElementos
        );
        
        return ListarPrestamosResultDto.builder()
                .paginaPrestamos(pagina)
                .resumen(resumen)
                .build();
    }
}