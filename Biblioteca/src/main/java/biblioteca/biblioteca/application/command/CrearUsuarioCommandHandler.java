package biblioteca.biblioteca.application.command;

import biblioteca.biblioteca.web.dto.UsuarioDto;
import biblioteca.biblioteca.web.mapper.UsuarioDtoMapper;
import biblioteca.biblioteca.domain.exception.DatoInvalidoException;
import biblioteca.biblioteca.domain.model.Usuario;
import biblioteca.biblioteca.domain.port.IUsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CrearUsuarioCommandHandler {

    private final IUsuarioRepository usuarioRepo;
    private final UsuarioDtoMapper dtoMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioDto handle(CrearUsuarioCommand cmd) {
        if (cmd == null) throw new DatoInvalidoException("El comando no puede ser null");
        // Se asume que cmd.passwordHash contiene la contraseña en texto plano.
        var hash = passwordEncoder.encode(cmd.getPasswordHash());
        var nuevo = Usuario.nuevo(
            cmd.getNombreCompleto(),
            cmd.getDni(),
            cmd.getEmail(),
            hash,
            cmd.getRoles(),
            cmd.getLectorId()
        );
        var guardado = usuarioRepo.guardar(nuevo);
        return dtoMapper.toDto(guardado);
    }
}
