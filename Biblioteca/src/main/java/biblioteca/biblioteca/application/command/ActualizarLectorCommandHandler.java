package biblioteca.biblioteca.application.command;

import biblioteca.biblioteca.application.exception.EntidadNoEncontradaException;
import biblioteca.biblioteca.domain.exception.DatoInvalidoException;
import biblioteca.biblioteca.domain.port.ILectorRepository;
import biblioteca.biblioteca.web.dto.LectorDto;
import biblioteca.biblioteca.web.mapper.LectorDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActualizarLectorCommandHandler {

    private final ILectorRepository lectorRepo;
    private final LectorDtoMapper dtoMapper;

    @Transactional
    public LectorDto handle(ActualizarLectorCommand cmd) {
        if (cmd == null) throw new DatoInvalidoException("El comando no puede ser null");
        var lector = lectorRepo.porId(cmd.getIdLector());
        if (lector == null) throw new EntidadNoEncontradaException("Lector inexistente: " + cmd.getIdLector());

        // Requiere que Lector del dominio tenga: public void actualizarNombre(String)
        lector.actualizarNombre(cmd.getNuevoNombre());

        lectorRepo.guardar(lector);
        return dtoMapper.toDto(lector);
    }
}