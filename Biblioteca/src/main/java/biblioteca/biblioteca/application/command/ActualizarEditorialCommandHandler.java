package biblioteca.biblioteca.application.command;

import biblioteca.biblioteca.application.exception.EntidadNoEncontradaException;
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
public class ActualizarEditorialCommandHandler {

    private final IEditorialRepository editorialRepo;
    private final EditorialDtoMapper dtoMapper;

    @Transactional
    public EditorialDto handle(ActualizarEditorialCommand cmd) {
        if (cmd == null) throw new DatoInvalidoException("El comando no puede ser null");
        Editorial e = editorialRepo.porId(cmd.getIdEditorial());
        if (e == null) throw new EntidadNoEncontradaException("Editorial inexistente: " + cmd.getIdEditorial());

        e.actualizarNombre(cmd.getNombre());
        Editorial actualizado = editorialRepo.guardar(e);
        return dtoMapper.toDto(actualizado);
    }
}
