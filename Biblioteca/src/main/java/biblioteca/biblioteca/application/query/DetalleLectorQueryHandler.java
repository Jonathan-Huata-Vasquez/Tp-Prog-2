package biblioteca.biblioteca.application.query;

import biblioteca.biblioteca.application.exception.EntidadNoEncontradaException;
import biblioteca.biblioteca.domain.port.ILectorRepository;
import biblioteca.biblioteca.domain.port.IPrestamoRepository;
import biblioteca.biblioteca.domain.port.ICopiaRepository;
import biblioteca.biblioteca.domain.port.ILibroRepository;
import biblioteca.biblioteca.domain.port.IAutorRepository;
import biblioteca.biblioteca.domain.model.Lector;
import biblioteca.biblioteca.domain.model.Prestamo;
import biblioteca.biblioteca.domain.model.Copia;
import biblioteca.biblioteca.domain.model.Libro;
import biblioteca.biblioteca.domain.model.Autor;
import biblioteca.biblioteca.web.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handler para consulta de detalle de lector.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DetalleLectorQueryHandler {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    private final ILectorRepository lectorRepository;
    private final IPrestamoRepository prestamoRepository;
    private final ICopiaRepository copiaRepository;
    private final ILibroRepository libroRepository;
    private final IAutorRepository autorRepository;

    public DetalleLectorResultDto handle(DetalleLectorQuery query) {
        log.debug("Ejecutando query de detalle para lector ID: {}", query.getIdLector());

        // Buscar lector usando repositorio de dominio
        Lector lector = lectorRepository.porId(query.getIdLector());
        if (lector == null) {
            log.error("Lector no encontrado con ID: {}", query.getIdLector());
            throw new EntidadNoEncontradaException("Lector no encontrado con ID: " + query.getIdLector());
        }
        
        log.debug("Lector encontrado: {} - Bloqueado: {}", lector.getNombre(), lector.getBloqueadoHasta());
        
        LocalDate hoy = LocalDate.now();
        
        // Convertir a DTO
        LectorDetalleDto lectorDto = convertirADetalle(lector, hoy);
        log.debug("LectorDto creado - ID: {}, Bloqueado: {}, Días bloqueo: {}", 
                 lectorDto.getIdLector(), lectorDto.isBloqueado(), lectorDto.getDiasBloqueo());
        
        // Obtener préstamos del lector usando repositorio de dominio
        List<Prestamo> todosLosPrestamos = prestamoRepository.todosLosPorLectorOrdenadosPorVencimiento(query.getIdLector());
        log.debug("Préstamos encontrados para lector {}: {}", query.getIdLector(), todosLosPrestamos.size());
        
        // Separar préstamos activos/vencidos y devueltos
        List<PrestamoLectorDto> prestamosActivos = todosLosPrestamos.stream()
                .filter(Prestamo::estaAbierto)
                .map(p -> convertirAPrestamoDto(p, hoy))
                .collect(Collectors.toList());
        log.debug("Préstamos activos procesados: {}", prestamosActivos.size());
                
        List<PrestamoLectorDto> prestamosDevueltos = todosLosPrestamos.stream()
                .filter(p -> !p.estaAbierto())
                .map(p -> convertirAPrestamoDto(p, hoy))
                .collect(Collectors.toList());
        log.debug("Préstamos devueltos procesados: {}", prestamosDevueltos.size());
        
        // Crear resumen usando métodos optimizados del repositorio
        ResumenLectorDto resumen = crearResumenLector(query.getIdLector());
        log.debug("Resumen creado - Activos: {}, Vencidos: {}, Devueltos: {}", 
                 resumen.getPrestamosActivos(), resumen.getPrestamosVencidos(), resumen.getTotalDevueltos());
        
        DetalleLectorResultDto resultado = DetalleLectorResultDto.builder()
                .lector(lectorDto)
                .resumen(resumen)
                .prestamosActivos(prestamosActivos)
                .prestamosDevueltos(prestamosDevueltos)
                .build();
                
        log.debug("DetalleLectorResultDto creado exitosamente");
        return resultado;
    }

    private LectorDetalleDto convertirADetalle(Lector lector, LocalDate hoy) {
        boolean estaBloqueado = !lector.puedePedir(hoy) && lector.getBloqueadoHasta() != null;
        
        int diasBloqueo = 0;
        if (estaBloqueado && lector.getBloqueadoHasta() != null) {
            diasBloqueo = (int) ChronoUnit.DAYS.between(hoy, lector.getBloqueadoHasta());
        }
        
        return LectorDetalleDto.builder()
                .idLector(lector.getIdLector())
                .nombreCompleto(lector.getNombre())
                .bloqueado(estaBloqueado)
                .bloqueadoHasta(lector.getBloqueadoHasta())
                .diasBloqueo(diasBloqueo)
                .build();
    }

    private PrestamoLectorDto convertirAPrestamoDto(Prestamo prestamo, LocalDate hoy) {
        log.debug("Convirtiendo préstamo ID: {} - Copia ID: {}", prestamo.getIdPrestamo(), prestamo.getIdCopia());
        
        String estado;
        
        if (!prestamo.estaAbierto()) {
            estado = "DEVUELTO";
        } else if (prestamo.diasAtrasoAl(hoy) > 0) {
            estado = "VENCIDO";
        } else {
            estado = "ACTIVO";
        }
        
        // Obtener información del libro y autor a través de la copia
        String tituloLibro = "Libro no encontrado";
        String autorNombre = "Autor no encontrado";
        
        try {
            Copia copia = copiaRepository.porId(prestamo.getIdCopia());
            if (copia != null) {
                log.debug("Copia encontrada ID: {} - Libro ID: {}", copia.getIdCopia(), copia.getIdLibro());
                Libro libro = libroRepository.porId(copia.getIdLibro());
                if (libro != null) {
                    tituloLibro = libro.getTitulo();
                    log.debug("Libro encontrado: {} - Autor ID: {}", tituloLibro, libro.getIdAutor());
                    
                    Autor autor = autorRepository.porId(libro.getIdAutor());
                    if (autor != null) {
                        autorNombre = autor.getNombre();
                        log.debug("Autor encontrado: {}", autorNombre);
                    } else {
                        log.warn("Autor no encontrado para ID: {}", libro.getIdAutor());
                    }
                } else {
                    log.warn("Libro no encontrado para ID: {}", copia.getIdLibro());
                }
            } else {
                log.warn("Copia no encontrada para ID: {}", prestamo.getIdCopia());
            }
        } catch (Exception e) {
            log.error("Error al obtener información del libro para préstamo {}: {}", prestamo.getIdPrestamo(), e.getMessage());
        }
        
        Integer diasAtraso = null;
        if ("VENCIDO".equals(estado)) {
            diasAtraso = (int) java.time.temporal.ChronoUnit.DAYS.between(prestamo.getFechaVencimiento(), hoy);
            if (diasAtraso < 0) diasAtraso = 0;
        }
        PrestamoLectorDto dto = PrestamoLectorDto.builder()
            .idPrestamo(prestamo.getIdPrestamo())
            .tituloLibro(tituloLibro)
            .autorNombre(autorNombre)
            .idEjemplar(prestamo.getIdCopia())
            .fechaPrestamo(prestamo.getFechaInicio())
            .fechaVencimiento(prestamo.getFechaVencimiento())
            .fechaDevolucion(prestamo.getFechaDevolucion())
            .estado(estado)
            .diasAtraso(diasAtraso)
            .build();
                
        log.debug("PrestamoDto creado - ID: {}, Título: {}, Estado: {}", 
                 dto.getIdPrestamo(), dto.getTituloLibro(), dto.getEstado());
        
        return dto;
    }

    private ResumenLectorDto crearResumenLector(Integer idLector) {
        // Usar métodos optimizados del repositorio en lugar de procesar todos los préstamos
        int activos = prestamoRepository.contarActivosPorLector(idLector);
        int vencidos = prestamoRepository.contarVencidosPorLector(idLector);
        int devueltos = prestamoRepository.contarDevueltosPorLector(idLector);
        
        return ResumenLectorDto.builder()
                .prestamosActivos(activos)
                .prestamosVencidos(vencidos)
                .totalDevueltos(devueltos)
                .build();
    }
}