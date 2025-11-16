package biblioteca.biblioteca.application.query;

import biblioteca.biblioteca.application.exception.EntidadNoEncontradaException;
import biblioteca.biblioteca.domain.model.*;
import biblioteca.biblioteca.domain.port.*;
import biblioteca.biblioteca.web.dto.PrestamoDetalleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class PrestamoDetalleQueryHandler {

    private final IPrestamoRepository prestamoRepo;
    private final ICopiaRepository copiaRepo;
    private final ILibroRepository libroRepo;
    private final IAutorRepository autorRepo;
    private final IEditorialRepository editorialRepo;
    private final ILectorRepository lectorRepo;

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PrestamoDetalleDto handle(PrestamoDetalleQuery query) {
        if (query == null || query.getIdPrestamo() == null) {
            throw new EntidadNoEncontradaException("ID de préstamo requerido");
        }

        // Obtener el préstamo
        Prestamo prestamo = prestamoRepo.porId(query.getIdPrestamo());
        if (prestamo == null) {
            throw new EntidadNoEncontradaException("Préstamo no encontrado: " + query.getIdPrestamo());
        }

        // Obtener información de la copia y libro
        Copia copia = copiaRepo.porId(prestamo.getIdCopia());
        if (copia == null) {
            throw new EntidadNoEncontradaException("Copia no encontrada: " + prestamo.getIdCopia());
        }

        Libro libro = libroRepo.porId(copia.getIdLibro());
        if (libro == null) {
            throw new EntidadNoEncontradaException("Libro no encontrado: " + copia.getIdLibro());
        }

        Autor autor = autorRepo.porId(libro.getIdAutor());
        if (autor == null) {
            throw new EntidadNoEncontradaException("Autor no encontrado: " + libro.getIdAutor());
        }

        Editorial editorial = editorialRepo.porId(libro.getIdEditorial());
        if (editorial == null) {
            throw new EntidadNoEncontradaException("Editorial no encontrada: " + libro.getIdEditorial());
        }

        // Obtener información del lector
        Lector lector = lectorRepo.porId(prestamo.getIdLector());
        if (lector == null) {
            throw new EntidadNoEncontradaException("Lector no encontrado: " + prestamo.getIdLector());
        }

        // Determinar estado y días de atraso
        EstadoPrestamo estado = determinarEstado(prestamo);
        int diasAtraso = prestamo.diasAtrasoAl(LocalDate.now());

        return PrestamoDetalleDto.builder()
                .codigo(prestamo.getIdPrestamo().toString())
                .estado(estado.name())
                .diasAtraso(diasAtraso)
                .fechaPrestamo(prestamo.getFechaInicio().format(FORMATO_FECHA))
                .fechaVencimiento(prestamo.getFechaVencimiento().format(FORMATO_FECHA))
                .fechaDevolucion(prestamo.getFechaDevolucion() != null ? 
                    prestamo.getFechaDevolucion().format(FORMATO_FECHA) : null)
                .tituloLibro(libro.getTitulo())
                .autor(autor.getNombre())
                .editorial(editorial.getNombre())
                .anioPublicacion(String.valueOf(libro.getAnioPublicacion()))
                .urlPortada(null) // TODO: Implementar cuando se agregue a LibroEntity
                .idLector(prestamo.getIdLector())
                .nombreLector(lector.getNombre())
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