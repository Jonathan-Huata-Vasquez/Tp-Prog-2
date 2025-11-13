package biblioteca.biblioteca.application.command;

import biblioteca.biblioteca.domain.exception.DatoInvalidoException;
import biblioteca.biblioteca.domain.model.Copia;
import biblioteca.biblioteca.domain.port.ICopiaRepository;
import biblioteca.biblioteca.web.dto.CopiaDto;
import biblioteca.biblioteca.web.mapper.CopiaDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CrearCopiaCommandHandler {

    private final ICopiaRepository copiaRepo;
    private final CopiaDtoMapper dtoMapper;

    @Transactional
    public CopiaDto handle(CrearCopiaCommand cmd) {
        if (cmd == null) throw new DatoInvalidoException("El comando no puede ser null");
        var nueva = Copia.nueva(cmd.getIdLibro());
        var guardada = copiaRepo.guardar(nueva); // retorna con ID
        return dtoMapper.toDto(guardada);
    }
}
