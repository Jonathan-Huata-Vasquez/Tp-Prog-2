package biblioteca.biblioteca.application.command;

import biblioteca.biblioteca.application.exception.EntidadNoEncontradaException;
import biblioteca.biblioteca.domain.exception.DatoInvalidoException;
import biblioteca.biblioteca.domain.port.IAutorRepository;
import biblioteca.biblioteca.domain.port.ILibroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EliminarAutorCommandHandler {

    private final IAutorRepository autorRepo;
    private final ILibroRepository libroRepo;

    @Transactional
    public void handle(EliminarAutorCommand cmd) {
        if (cmd == null) throw new DatoInvalidoException("El comando no puede ser null");
        var existente = autorRepo.porId(cmd.getIdAutor());
        if (existente == null) throw new EntidadNoEncontradaException("Autor inexistente: " + cmd.getIdAutor());

        int tieneLibros = libroRepo.contarAutorPorLibro(cmd.getIdAutor());
        if (tieneLibros > 0) {
            throw new DatoInvalidoException("No se puede eliminar el libro porque tiene copias asociadas.");
        }

        autorRepo.eliminar(cmd.getIdAutor());


    }
}
