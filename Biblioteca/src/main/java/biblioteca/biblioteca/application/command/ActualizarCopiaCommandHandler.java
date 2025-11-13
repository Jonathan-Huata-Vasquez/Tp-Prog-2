package biblioteca.biblioteca.application.command;

import biblioteca.biblioteca.application.exception.EntidadNoEncontradaException;
import biblioteca.biblioteca.application.exception.OperacionNoPermitidaException;
import biblioteca.biblioteca.domain.exception.DatoInvalidoException;
import biblioteca.biblioteca.domain.model.Copia;
import biblioteca.biblioteca.domain.model.EstadoCopia;
import biblioteca.biblioteca.domain.port.ICopiaRepository;
import biblioteca.biblioteca.web.dto.CopiaDto;
import biblioteca.biblioteca.web.mapper.CopiaDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActualizarCopiaCommandHandler {

    private final ICopiaRepository copiaRepo;
    private final CopiaDtoMapper dtoMapper;

    @Transactional
    public CopiaDto handle(ActualizarCopiaCommand cmd) {
        if (cmd == null) throw new DatoInvalidoException("El comando no puede ser null");

        Copia c = copiaRepo.porId(cmd.getIdCopia());
        if (c == null) throw new EntidadNoEncontradaException("Copia inexistente: " + cmd.getIdCopia());

        // Solo permitimos cambiar a EnBiblioteca o EnReparacion desde este CRUD
        if (cmd.getNuevoEstado() == EstadoCopia.EnBiblioteca) {
            c.marcarDisponible();
        } else if (cmd.getNuevoEstado() == EstadoCopia.EnReparacion) {
            c.marcarEnReparacion();
        } else {
            throw new OperacionNoPermitidaException("El estado solo puede cambiarse a EnBiblioteca o EnReparacion desde el CRUD");
        }

        var actualizada = copiaRepo.guardar(c);
        return dtoMapper.toDto(actualizada);
    }
}
