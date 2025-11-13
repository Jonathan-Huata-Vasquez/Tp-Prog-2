package biblioteca.biblioteca.application.command;

import biblioteca.biblioteca.application.exception.EntidadNoEncontradaException;
import biblioteca.biblioteca.domain.exception.DatoInvalidoException;
import biblioteca.biblioteca.domain.port.ICopiaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EliminarCopiaCommandHandler {

    private final ICopiaRepository copiaRepo;

    @Transactional
    public void handle(EliminarCopiaCommand cmd) {
        if (cmd == null) throw new DatoInvalidoException("El comando no puede ser null");
        var existente = copiaRepo.porId(cmd.getIdCopia());
        if (existente == null) throw new EntidadNoEncontradaException("Copia inexistente: " + cmd.getIdCopia());
        copiaRepo.eliminar(cmd.getIdCopia());
    }
}
