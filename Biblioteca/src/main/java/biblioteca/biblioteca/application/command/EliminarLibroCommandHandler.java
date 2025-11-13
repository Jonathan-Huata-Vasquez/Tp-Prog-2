package biblioteca.biblioteca.application.command;

import biblioteca.biblioteca.application.exception.EntidadNoEncontradaException;
import biblioteca.biblioteca.domain.exception.DatoInvalidoException;
import biblioteca.biblioteca.domain.port.ILibroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EliminarLibroCommandHandler {

    private final ILibroRepository libroRepo;

    @Transactional
    public void handle(EliminarLibroCommand cmd) {
        if (cmd == null) throw new DatoInvalidoException("El comando no puede ser null");
        var existente = libroRepo.porId(cmd.getIdLibro());
        if (existente == null) throw new EntidadNoEncontradaException("Libro inexistente: " + cmd.getIdLibro());
        libroRepo.eliminar(cmd.getIdLibro());
    }
}
