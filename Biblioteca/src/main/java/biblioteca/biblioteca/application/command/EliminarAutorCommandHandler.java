package biblioteca.biblioteca.application.command;

import biblioteca.biblioteca.application.exception.EntidadNoEncontradaException;
import biblioteca.biblioteca.domain.exception.DatoInvalidoException;
import biblioteca.biblioteca.domain.port.IAutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EliminarAutorCommandHandler {

    private final IAutorRepository autorRepo;

    @Transactional
    public void handle(EliminarAutorCommand cmd) {
        if (cmd == null) throw new DatoInvalidoException("El comando no puede ser null");
        var existente = autorRepo.porId(cmd.getIdAutor());
        if (existente == null) throw new EntidadNoEncontradaException("Autor inexistente: " + cmd.getIdAutor());
        autorRepo.eliminar(cmd.getIdAutor());
    }
}
