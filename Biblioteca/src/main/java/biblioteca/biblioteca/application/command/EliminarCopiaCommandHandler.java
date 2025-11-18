package biblioteca.biblioteca.application.command;

import biblioteca.biblioteca.application.exception.EntidadNoEncontradaException;
import biblioteca.biblioteca.domain.exception.DatoInvalidoException;
import biblioteca.biblioteca.domain.port.ICopiaRepository;
import biblioteca.biblioteca.domain.port.IPrestamoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EliminarCopiaCommandHandler {

    private final ICopiaRepository copiaRepo;
    private final IPrestamoRepository prestamoRepo;

    @Transactional
    public void handle(EliminarCopiaCommand cmd) {
        if (cmd == null) throw new DatoInvalidoException("El comando no puede ser null");
        var existente = copiaRepo.porId(cmd.getIdCopia());
        if (existente == null) throw new EntidadNoEncontradaException("Copia inexistente: " + cmd.getIdCopia());

        var tienePrestamos = prestamoRepo.contarCopiaEnPrestamos(cmd.getIdCopia()) > 0;
        if (tienePrestamos) throw new DatoInvalidoException("No se puede eliminar el copia porque tiene prestamos asociados.");
        copiaRepo.eliminar(cmd.getIdCopia());
    }
}
