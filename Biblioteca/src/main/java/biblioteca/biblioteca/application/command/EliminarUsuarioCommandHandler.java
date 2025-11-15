package biblioteca.biblioteca.application.command;

import biblioteca.biblioteca.application.exception.EntidadNoEncontradaException;
import biblioteca.biblioteca.domain.exception.DatoInvalidoException;
import biblioteca.biblioteca.domain.port.IUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EliminarUsuarioCommandHandler {

    private final IUsuarioRepository usuarioRepo;

    @Transactional
    public void handle(EliminarUsuarioCommand cmd) {
        if (cmd == null) throw new DatoInvalidoException("El comando no puede ser null");
        var existente = usuarioRepo.porId(cmd.getIdUsuario());
        if (existente == null) throw new EntidadNoEncontradaException("Usuario inexistente: " + cmd.getIdUsuario());
        usuarioRepo.eliminar(cmd.getIdUsuario());
    }
}
