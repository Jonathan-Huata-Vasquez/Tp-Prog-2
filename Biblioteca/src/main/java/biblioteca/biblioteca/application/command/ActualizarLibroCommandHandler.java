package biblioteca.biblioteca.application.command;

import biblioteca.biblioteca.application.exception.EntidadNoEncontradaException;
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
public class ActualizarLibroCommandHandler {

    private final ILibroRepository libroRepo;
    private final LibroDtoMapper dtoMapper;

    @Transactional
    public LibroDto handle(ActualizarLibroCommand cmd) {
        if (cmd == null) throw new DatoInvalidoException("El comando no puede ser null");
        Libro l = libroRepo.porId(cmd.getIdLibro());
        if (l == null) throw new EntidadNoEncontradaException("Libro inexistente: " + cmd.getIdLibro());

        l.actualizarMetadatos(cmd.getTitulo(), cmd.getAnioPublicacion(),
                cmd.getIdAutor(), cmd.getIdEditorial(), cmd.getCategoria());

        Libro actualizado = libroRepo.guardar(l);
        return dtoMapper.toDto(actualizado);
    }
}
