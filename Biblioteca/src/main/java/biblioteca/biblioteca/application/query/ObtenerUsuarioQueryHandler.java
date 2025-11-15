package biblioteca.biblioteca.application.query;

import biblioteca.biblioteca.application.exception.EntidadNoEncontradaException;
import biblioteca.biblioteca.web.dto.UsuarioDto;
import biblioteca.biblioteca.web.mapper.UsuarioDtoMapper;
import biblioteca.biblioteca.domain.exception.DatoInvalidoException;
import biblioteca.biblioteca.domain.port.IUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ObtenerUsuarioQueryHandler {

    private final IUsuarioRepository usuarioRepo;
    private final UsuarioDtoMapper dtoMapper;

    @Transactional(readOnly = true)
    public UsuarioDto handle(ObtenerUsuarioQuery q) {
        if (q == null) throw new DatoInvalidoException("La query no puede ser null");
        var u = usuarioRepo.porId(q.getIdUsuario());
        if (u == null) throw new EntidadNoEncontradaException("Usuario inexistente: " + q.getIdUsuario());
        return dtoMapper.toDto(u);
    }
}
