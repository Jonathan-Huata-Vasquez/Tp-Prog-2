package biblioteca.biblioteca.application.command;

import biblioteca.biblioteca.domain.exception.DatoInvalidoException;
import biblioteca.biblioteca.domain.model.Autor;
import biblioteca.biblioteca.domain.port.IAutorRepository;
import biblioteca.biblioteca.web.dto.AutorDto;
import biblioteca.biblioteca.web.mapper.AutorDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CrearAutorCommandHandler {

    private final IAutorRepository autorRepo;
    private final AutorDtoMapper dtoMapper;

    @Transactional
    public AutorDto handle(CrearAutorCommand cmd) {
        if (cmd == null) throw new DatoInvalidoException("El comando no puede ser null");
        var nuevo = Autor.nuevo(cmd.getNombre(), cmd.getFechaNacimiento(), cmd.getNacionalidad());
        var guardado = autorRepo.guardar(nuevo);
        return dtoMapper.toDto(guardado);
    }
}
