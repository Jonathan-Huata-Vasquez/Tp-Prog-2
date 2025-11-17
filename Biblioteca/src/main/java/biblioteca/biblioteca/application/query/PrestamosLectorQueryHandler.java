package biblioteca.biblioteca.application.query;

import biblioteca.biblioteca.application.exception.EntidadNoEncontradaException;
import biblioteca.biblioteca.domain.exception.DatoInvalidoException;
import biblioteca.biblioteca.domain.model.Prestamo;
import biblioteca.biblioteca.domain.model.Libro;
import biblioteca.biblioteca.domain.model.Copia;
import biblioteca.biblioteca.domain.model.Autor;
import biblioteca.biblioteca.domain.model.EstadoPrestamo;
import biblioteca.biblioteca.domain.port.IPrestamoRepository;
import biblioteca.biblioteca.domain.port.ILibroRepository;
import biblioteca.biblioteca.domain.port.ICopiaRepository;
import biblioteca.biblioteca.domain.port.IAutorRepository;
import biblioteca.biblioteca.web.dto.PrestamoLectorDto;
import biblioteca.biblioteca.web.dto.PrestamosLectorResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrestamosLectorQueryHandler {

    private final IPrestamoRepository prestamoRepo;
    private final ICopiaRepository copiaRepo;
    private final ILibroRepository libroRepo;
    private final IAutorRepository autorRepo;

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Transactional(readOnly = true)
    public PrestamosLectorResultDto handle(PrestamosLectorQuery query) {
        if (query == null || query.getIdLector() == null) {
            throw new DatoInvalidoException("El ID del lector es requerido");
        }

        Integer idLector = query.getIdLector();

        // Obtener préstamos ordenados por BD (activos primero, luego por fecha vencimiento)
        List<Prestamo> prestamos = prestamoRepo.todosLosPorLectorOrdenadosPorVencimiento(idLector);

        // Convertir a DTOs (ya vienen ordenados)
        List<PrestamoLectorDto> prestamosDto = prestamos.stream()
                .map(this::mapearPrestamoADto)
                .collect(Collectors.toList());

        // Calcular resumen usando queries optimizadas
        int activos = prestamoRepo.contarActivosPorLector(idLector);
        int vencidos = prestamoRepo.contarVencidosPorLector(idLector);
        int devueltos = prestamoRepo.contarDevueltosPorLector(idLector);

        PrestamosLectorResultDto.ResumenPrestamosDto resumen = PrestamosLectorResultDto.ResumenPrestamosDto.builder()
                .cantidadActivos(activos)
                .cantidadVencidos(vencidos)
                .cantidadDevueltos(devueltos)
                .build();

        return PrestamosLectorResultDto.builder()
                .resumen(resumen)
                .prestamos(prestamosDto)
                .build();
    }

    private PrestamoLectorDto mapearPrestamoADto(Prestamo prestamo) {
        // Obtener información de la copia y libro
        Copia copia = copiaRepo.porId(prestamo.getIdCopia());
        if (copia == null) throw new EntidadNoEncontradaException("Copia no encontrada: " + prestamo.getIdCopia());
        
        Libro libro = libroRepo.porId(copia.getIdLibro());
        if (libro == null) throw new EntidadNoEncontradaException("Libro no encontrado: " + copia.getIdLibro());
        
        Autor autor = autorRepo.porId(libro.getIdAutor());
        if (autor == null) throw new EntidadNoEncontradaException("Autor no encontrado: " + libro.getIdAutor());

        // Determinar estado
        String estado = determinarEstado(prestamo).name();

        return PrestamoLectorDto.builder()
                .idPrestamo(prestamo.getIdPrestamo())
                .tituloLibro(libro.getTitulo())
                .autorNombre(autor.getNombre())
                .idEjemplar(copia.getIdCopia())
                .fechaPrestamo(prestamo.getFechaInicio())
                .fechaVencimiento(prestamo.getFechaVencimiento())
                .fechaDevolucion(prestamo.getFechaDevolucion())
                .estado(estado)
                .build();
    }

    private EstadoPrestamo determinarEstado(Prestamo prestamo) {
        if (prestamo.getFechaDevolucion() != null) {
            return EstadoPrestamo.DEVUELTO;
        }
        
        LocalDate hoy = LocalDate.now();
        if (prestamo.getFechaVencimiento().isBefore(hoy)) {
            return EstadoPrestamo.VENCIDO;
        }
        
        return EstadoPrestamo.ACTIVO;
    }
}