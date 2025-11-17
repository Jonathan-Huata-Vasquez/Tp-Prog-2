package biblioteca.biblioteca.application.query;

import biblioteca.biblioteca.domain.model.*;
import biblioteca.biblioteca.domain.port.*;
import biblioteca.biblioteca.web.dto.PrestamoBibliotecarioDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValidarDevolucionQueryHandler {

    private final IPrestamoRepository prestamoRepository;
    private final ILectorRepository lectorRepository;
    private final ICopiaRepository copiaRepository;
    private final ILibroRepository libroRepository;
    private final IAutorRepository autorRepository;
    private final IEditorialRepository editorialRepository;

    @Transactional(readOnly = true)
    public ValidarDevolucionResultDto handle(ValidarDevolucionQuery query) {
        if (query == null || query.getIdLector() == null || query.getIdCopia() == null) {
            return ValidarDevolucionResultDto.builder()
                    .puedeDevolver(false)
                    .mensajeError("Debe proporcionar idLector e idCopia")
                    .build();
        }
        try {
            Prestamo prestamo = prestamoRepository.activoPor(query.getIdLector(), query.getIdCopia());
            if (prestamo == null) {
                return ValidarDevolucionResultDto.builder()
                        .puedeDevolver(false)
                        .mensajeError("No existe préstamo activo para los datos ingresados")
                        .build();
            }

            if (!prestamo.estaAbierto()) {
                return ValidarDevolucionResultDto.builder()
                        .puedeDevolver(false)
                        .mensajeError("Este prestamo ya fue cerrado")
                        .build();
            }
            // Construir resumen
            Lector lector = lectorRepository.porId(prestamo.getIdLector());
            Copia copia = copiaRepository.porId(prestamo.getIdCopia());
            Libro libro = copia != null ? libroRepository.porId(copia.getIdLibro()) : null;
            Autor autor = libro != null ? autorRepository.porId(libro.getIdAutor()) : null;
            Editorial editorial = libro != null ? editorialRepository.porId(libro.getIdEditorial()) : null;

            LocalDate hoy = LocalDate.now();
            String estado;
            if (prestamo.getFechaDevolucion() != null) {
                estado = EstadoPrestamo.DEVUELTO.name();
            } else if (prestamo.getFechaVencimiento().isBefore(hoy)) {
                estado = EstadoPrestamo.VENCIDO.name();
            } else {
                estado = EstadoPrestamo.ACTIVO.name();
            }
            int diasAtraso = prestamo.diasAtrasoAl(hoy);

            PrestamoBibliotecarioDto resumen = PrestamoBibliotecarioDto.builder()
                    .idPrestamo(prestamo.getIdPrestamo())
                    .idCopia(prestamo.getIdCopia())
                    .fechaInicio(prestamo.getFechaInicio())
                    .fechaVencimiento(prestamo.getFechaVencimiento())
                    .fechaDevolucion(prestamo.getFechaDevolucion())
                    .estado(estado)
                    .diasAtraso(diasAtraso)
                    .idLector(prestamo.getIdLector())
                    .nombreLector(lector != null ? lector.getNombre() : null)
                    .lectorBloqueado(lector != null ? (lector.getBloqueadoHasta() != null && hoy.isBefore(lector.getBloqueadoHasta())) : null)
                    .tituloLibro(libro != null ? libro.getTitulo() : null)
                    .autorNombre(autor != null ? autor.getNombre() : null)
                    .editorialNombre(editorial != null ? editorial.getNombre() : null)
                    .build();

            boolean puedeDevolver = prestamo.getFechaDevolucion() == null; // solo si sigue abierto
            String mensajeError = puedeDevolver ? null : "El préstamo ya fue devuelto";
            return ValidarDevolucionResultDto.builder()
                    .puedeDevolver(puedeDevolver)
                    .resumenPrestamo(resumen)
                    .mensajeError(mensajeError)
                    .build();
        } catch (Exception e) {
            log.error("Error validando devolución", e);
            return ValidarDevolucionResultDto.builder()
                    .puedeDevolver(false)
                    .mensajeError("Error al validar: " + e.getMessage())
                    .build();
        }
    }
}
