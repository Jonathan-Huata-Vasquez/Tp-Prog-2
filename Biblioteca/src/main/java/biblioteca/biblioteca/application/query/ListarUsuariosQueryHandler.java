package biblioteca.biblioteca.application.query;

import biblioteca.biblioteca.web.dto.UsuarioDto;
import biblioteca.biblioteca.web.mapper.UsuarioDtoMapper;
import biblioteca.biblioteca.domain.port.IUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListarUsuariosQueryHandler {

    private final IUsuarioRepository usuarioRepo;
    private final UsuarioDtoMapper dtoMapper;

    @Transactional(readOnly = true)
    public List<UsuarioDto> handle(ListarUsuariosQuery q) {
        return usuarioRepo.todos().stream().map(dtoMapper::toDto).toList();
    }
}
