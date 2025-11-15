package biblioteca.biblioteca.application.command;

import biblioteca.biblioteca.application.exception.EntidadNoEncontradaException;
import biblioteca.biblioteca.domain.exception.DatoInvalidoException;
import biblioteca.biblioteca.domain.model.Usuario;
import biblioteca.biblioteca.domain.port.IUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActualizarPasswordCommandHandler {

    private final IUsuarioRepository usuarioRepo;

    @Transactional
    public void handle(ActualizarPasswordCommand cmd) {
        if (cmd == null) throw new DatoInvalidoException("El comando no puede ser null");
        Usuario u = usuarioRepo.porId(cmd.getIdUsuario());
        if (u == null) throw new EntidadNoEncontradaException("Usuario inexistente: " + cmd.getIdUsuario());
        u.actualizarPasswordHash(cmd.getPasswordHash());
        usuarioRepo.guardar(u);
    }
}
