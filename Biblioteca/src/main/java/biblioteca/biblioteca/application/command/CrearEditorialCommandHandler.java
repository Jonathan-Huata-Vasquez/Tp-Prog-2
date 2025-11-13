package biblioteca.biblioteca.application.command;

import biblioteca.biblioteca.domain.exception.DatoInvalidoException;
import biblioteca.biblioteca.domain.model.Editorial;
import biblioteca.biblioteca.domain.port.IEditorialRepository;
import biblioteca.biblioteca.web.dto.EditorialDto;
import biblioteca.biblioteca.web.mapper.EditorialDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CrearEditorialCommandHandler {

    private final IEditorialRepository editorialRepo;
    private final EditorialDtoMapper dtoMapper;

    @Transactional
    public EditorialDto handle(CrearEditorialCommand cmd) {
        if (cmd == null) throw new DatoInvalidoException("El comando no puede ser null");
        Editorial nueva = Editorial.nuevo(cmd.getNombre());
        Editorial guardada = editorialRepo.guardar(nueva); // retorna con ID
        return dtoMapper.toDto(guardada);
    }
}
