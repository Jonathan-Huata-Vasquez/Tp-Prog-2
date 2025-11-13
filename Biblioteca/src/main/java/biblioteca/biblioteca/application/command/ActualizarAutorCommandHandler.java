package biblioteca.biblioteca.application.command;

import biblioteca.biblioteca.application.exception.EntidadNoEncontradaException;
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
public class ActualizarAutorCommandHandler {

    private final IAutorRepository autorRepo;
    private final AutorDtoMapper dtoMapper;

    @Transactional
    public AutorDto handle(ActualizarAutorCommand cmd) {
        if (cmd == null) throw new DatoInvalidoException("El comando no puede ser null");
        Autor a = autorRepo.porId(cmd.getIdAutor());
        if (a == null) throw new EntidadNoEncontradaException("Autor inexistente: " + cmd.getIdAutor());

        a.actualizarDatos(cmd.getNombre(), cmd.getFechaNacimiento(), cmd.getNacionalidad());
        var actualizado = autorRepo.guardar(a);
        return dtoMapper.toDto(actualizado);
    }
}
