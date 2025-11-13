package biblioteca.biblioteca.application.command;

import biblioteca.biblioteca.application.exception.EntidadNoEncontradaException;
import biblioteca.biblioteca.domain.exception.DatoInvalidoException;
import biblioteca.biblioteca.domain.model.Copia;
import biblioteca.biblioteca.domain.model.Lector;
import biblioteca.biblioteca.domain.model.Prestamo;
import biblioteca.biblioteca.domain.port.ICopiaRepository;
import biblioteca.biblioteca.domain.port.ILectorRepository;
import biblioteca.biblioteca.domain.port.IPrestamoRepository;
import biblioteca.biblioteca.web.dto.PrestamoDto;
import biblioteca.biblioteca.web.mapper.PrestamoDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DevolverCopiaCommandHandler {

    private final ILectorRepository lectorRepo;
    private final ICopiaRepository copiaRepo;
    private final IPrestamoRepository prestamoRepo;
    private final PrestamoDtoMapper dtoMapper;

    @Transactional
    public PrestamoDto handle(DevolverCopiaCommand cmd) {
        if (cmd == null) throw new DatoInvalidoException("El comando no puede ser null");

        Lector lector = lectorRepo.porId(cmd.getIdLector());
        if (lector == null) throw new EntidadNoEncontradaException("Lector inexistente: " + cmd.getIdLector());

        Copia copia = copiaRepo.porId(cmd.getIdCopia());
        if (copia == null) throw new EntidadNoEncontradaException("Copia inexistente: " + cmd.getIdCopia());

        //Verificacion que este activo
        Prestamo activo = prestamoRepo.activoPor(cmd.getIdLector(), cmd.getIdCopia());
        if (activo == null) throw new EntidadNoEncontradaException("No hay préstamo activo para esa copia y lector");

        LocalDate hoy = LocalDate.now();
        lector.registrarDevolucionEn(cmd.getIdCopia(), hoy);

        if (Boolean.TRUE.equals(cmd.getEnviarAReparacion())) {
            copia.marcarEnReparacion();
        } else {
            copia.marcarDevuelta(true);
        }

        Prestamo cerrado = prestamoRepo.guardar(activo);
        lectorRepo.guardar(lector);
        copiaRepo.guardar(copia);

        return dtoMapper.toDto(cerrado);
    }
}
