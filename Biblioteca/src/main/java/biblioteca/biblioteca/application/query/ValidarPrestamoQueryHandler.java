package biblioteca.biblioteca.application.query;

import biblioteca.biblioteca.domain.model.*;
import biblioteca.biblioteca.domain.port.*;
import biblioteca.biblioteca.web.dto.ResumenLectorValidacionDto;
import biblioteca.biblioteca.web.dto.ResumenEjemplarValidacionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Handler para validar datos de préstamo usando repositorios del dominio.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ValidarPrestamoQueryHandler {

    private final ILectorRepository lectorRepository;
    private final ICopiaRepository copiaRepository;
    private final ILibroRepository libroRepository;
    private final IAutorRepository autorRepository;
    private final IPrestamoRepository prestamoRepository;

    @Transactional(readOnly = true)
    public ValidarPrestamoResultDto handle(ValidarPrestamoQuery query) {
        log.debug("Validando préstamo: lector={}, copia={}", query.getIdLector(), query.getIdCopia());

        boolean lectorValido = false;
        boolean copiaValida = false;
        String mensajeError = null;
        ResumenLectorValidacionDto resumenLector = null;
        ResumenEjemplarValidacionDto resumenEjemplar = null;

        try {
            // Validar lector
            if (query.getIdLector() != null) {
                resumenLector = validarLector(query.getIdLector());
                if (resumenLector != null) {
                    lectorValido = !resumenLector.getBloqueado() && resumenLector.getPrestamosActivos() < 5;
                    log.debug("Lector {} validado: válido={}, préstamos activos={}, bloqueado={}", 
                             query.getIdLector(), lectorValido, resumenLector.getPrestamosActivos(), resumenLector.getBloqueado());
                } else {
                    lectorValido = false;
                    log.debug("Lector {} no encontrado", query.getIdLector());
                }
            }
            
            // Validar copia
            if (query.getIdCopia() != null) {
                resumenEjemplar = validarCopia(query.getIdCopia());
                if (resumenEjemplar != null) {
                    copiaValida = resumenEjemplar.getDisponible();
                    log.debug("Copia {} validada: válida={}, disponible={}", 
                             query.getIdCopia(), copiaValida, resumenEjemplar.getDisponible());
                } else {
                    copiaValida = false;
                    log.debug("Copia {} no encontrada", query.getIdCopia());
                }
            }

        } catch (Exception e) {
            log.error("Error al validar préstamo", e);
            mensajeError = "Error al validar: " + e.getMessage();
        }

        boolean puedeRegistrar = lectorValido && copiaValida && mensajeError == null;

        return ValidarPrestamoResultDto.builder()
                .lectorValido(lectorValido)
                .copiaValida(copiaValida)
                .puedeRegistrar(puedeRegistrar)
                .mensajeError(mensajeError)
                .resumenLector(resumenLector)
                .resumenEjemplar(resumenEjemplar)
                .build();
    }

    /**
     * Valida un lector para préstamo.
     */
    private ResumenLectorValidacionDto validarLector(Integer idLector) {
        try {
            Lector lector = lectorRepository.porId(idLector);
            if (lector == null) {
                return null;
            }

            int prestamosActivos = prestamoRepository.contarActivosPorLector(idLector);
            LocalDate hoy = LocalDate.now();
            boolean puedePrestar = lector.puedePedir(hoy);
            boolean bloqueado = !puedePrestar && lector.getBloqueadoHasta() != null && hoy.isBefore(lector.getBloqueadoHasta());
            String motivoBloqueo = null;
            
            if (!puedePrestar) {
                if (prestamosActivos >= 5) {
                    motivoBloqueo = "Alcanzó el máximo de 5 préstamos activos";
                } else if (bloqueado) {
                    motivoBloqueo = "Tiene penalización por atraso hasta " + lector.getBloqueadoHasta();
                }
            }

            return ResumenLectorValidacionDto.builder()
                    .id(lector.getIdLector())
                    .nombre(lector.getNombre())
                    .prestamosActivos(prestamosActivos)
                    .bloqueado(!puedePrestar)
                    .motivoBloqueo(motivoBloqueo)
                    .build();
        } catch (Exception e) {
            log.error("Error al validar lector {}", idLector, e);
            return null;
        }
    }

    /**
     * Valida una copia para préstamo.
     */
    private ResumenEjemplarValidacionDto validarCopia(Integer idCopia) {
        try {
            Copia copia = copiaRepository.porId(idCopia);
            if (copia == null) {
                return null;
            }

            Libro libro = libroRepository.porId(copia.getIdLibro());
            if (libro == null) {
                return null;
            }

            Autor autor = autorRepository.porId(libro.getIdAutor());
            String nombreAutor = autor != null ? autor.getNombre() : "Autor desconocido";

            boolean disponible = copia.getEstado() == EstadoCopia.EnBiblioteca;
            String estadoDetalle = disponible ? "Disponible" : "No disponible (" + copia.getEstado() + ")";

            return ResumenEjemplarValidacionDto.builder()
                    .idEjemplar(copia.getIdCopia())
                    .tituloLibro(libro.getTitulo())
                    .autor(nombreAutor)
                    .disponible(disponible)
                    .estadoDetalle(estadoDetalle)
                    .build();
        } catch (Exception e) {
            log.error("Error al validar copia {}", idCopia, e);
            return null;
        }
    }
}