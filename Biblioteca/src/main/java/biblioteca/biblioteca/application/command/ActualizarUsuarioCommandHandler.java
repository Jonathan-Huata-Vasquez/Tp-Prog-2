package biblioteca.biblioteca.application.command;

import biblioteca.biblioteca.application.exception.EntidadNoEncontradaException;
import biblioteca.biblioteca.web.dto.UsuarioDto;
import biblioteca.biblioteca.web.mapper.UsuarioDtoMapper;
import biblioteca.biblioteca.domain.exception.DatoInvalidoException;
import biblioteca.biblioteca.domain.model.Rol;
import biblioteca.biblioteca.domain.model.Usuario;
import biblioteca.biblioteca.domain.port.IUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActualizarUsuarioCommandHandler {

    private final IUsuarioRepository usuarioRepo;
    private final UsuarioDtoMapper dtoMapper;

    @Transactional
    public UsuarioDto handle(ActualizarUsuarioCommand cmd) {
        if (cmd == null) throw new DatoInvalidoException("El comando no puede ser null");
        Usuario u = usuarioRepo.porId(cmd.getIdUsuario());
        if (u == null) throw new EntidadNoEncontradaException("Usuario inexistente: " + cmd.getIdUsuario());

        u.actualizarNombreCompleto(cmd.getNombreCompleto());

        u.actualizarEmail(cmd.getEmail());
        u.actualizarRoles(cmd.getRoles());
        u.actualizarDni(cmd.getDni());

        var actualizado = usuarioRepo.guardar(u);
        return dtoMapper.toDto(actualizado);
    }
}
