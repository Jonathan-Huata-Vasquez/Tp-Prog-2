package biblioteca.biblioteca.application.command;

import biblioteca.biblioteca.domain.exception.DatoInvalidoException;
import biblioteca.biblioteca.domain.model.Libro;
import biblioteca.biblioteca.domain.port.ILibroRepository;
import biblioteca.biblioteca.web.dto.LibroDto;
import biblioteca.biblioteca.web.mapper.LibroDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CrearLibroCommandHandler {

    private final ILibroRepository libroRepo;
    private final LibroDtoMapper dtoMapper;

    @Transactional
    public LibroDto handle(CrearLibroCommand cmd) {
        if (cmd == null) throw new DatoInvalidoException("El comando no puede ser null");
        Libro nuevo = Libro.nuevo(cmd.getTitulo(), cmd.getAnioPublicacion(),
                cmd.getIdAutor(), cmd.getIdEditorial(), cmd.getCategoria());
        Libro guardado = libroRepo.guardar(nuevo); // retorna con ID
        return dtoMapper.toDto(guardado);
    }
}
