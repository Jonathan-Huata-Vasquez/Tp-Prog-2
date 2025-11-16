package biblioteca.biblioteca.application.query;

import biblioteca.biblioteca.application.exception.EntidadNoEncontradaException;
import biblioteca.biblioteca.domain.model.Lector;
import biblioteca.biblioteca.domain.model.Prestamo;
import biblioteca.biblioteca.domain.port.ILectorRepository;
import biblioteca.biblioteca.domain.port.IPrestamoRepository;
import biblioteca.biblioteca.web.dto.LectorDashboardDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LectorDashboardQueryHandler {

    private final ILectorRepository lectorRepo;
    private final IPrestamoRepository prestamoRepo;

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public LectorDashboardDto handle(LectorDashboardQuery query) {
        if (query == null || query.getIdLector() == null) {
            throw new EntidadNoEncontradaException("ID de lector requerido");
        }

        Integer idLector = query.getIdLector();
        LocalDate hoy = LocalDate.now();

        // Obtener el lector del dominio para verificar bloqueo
        Lector lector = lectorRepo.porId(idLector);
        if (lector == null) {
            throw new EntidadNoEncontradaException("Lector no encontrado: " + idLector);
        }

        // Calcular estado de bloqueo
        boolean bloqueado = !lector.puedePedir(hoy);
        String bloqueadoHasta = null;
        if (bloqueado && lector.getBloqueadoHasta() != null) {
            bloqueadoHasta = lector.getBloqueadoHasta().format(FORMATO_FECHA);
        }

        // Obtener préstamos activos
        List<Prestamo> prestamosActivos = prestamoRepo.activosPorLector(idLector);
        int cantidadActivos = prestamosActivos.size();

        // Encontrar próximo vencimiento
        String proximoVencimiento = null;
        if (!prestamosActivos.isEmpty()) {
            LocalDate fechaProximaVencimiento = prestamosActivos.stream()
                    .map(Prestamo::getFechaVencimiento)
                    .min(LocalDate::compareTo)
                    .orElse(null);
            
            if (fechaProximaVencimiento != null) {
                proximoVencimiento = fechaProximaVencimiento.format(FORMATO_FECHA);
            }
        }

        return LectorDashboardDto.builder()
                .bloqueado(bloqueado)
                .bloqueadoHasta(bloqueadoHasta)
                .prestamosActivos(cantidadActivos)
                .proximoVencimiento(proximoVencimiento)
                .build();
    }
}