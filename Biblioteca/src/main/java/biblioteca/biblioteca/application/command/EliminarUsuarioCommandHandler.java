package biblioteca.biblioteca.application.command;

import biblioteca.biblioteca.application.exception.EntidadNoEncontradaException;
import biblioteca.biblioteca.domain.exception.DatoInvalidoException;
import biblioteca.biblioteca.domain.exception.ReglaDeNegocioException;
import biblioteca.biblioteca.domain.model.Rol;
import biblioteca.biblioteca.domain.port.IUsuarioRepository;
import biblioteca.biblioteca.domain.port.IPrestamoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EliminarUsuarioCommandHandler {

    private final IUsuarioRepository usuarioRepo;
    private final IPrestamoRepository prestamoRepo;

    @Transactional
    public void handle(EliminarUsuarioCommand cmd) {
        if (cmd == null) throw new DatoInvalidoException("El comando no puede ser null");
        var existente = usuarioRepo.porId(cmd.getIdUsuario());
        if (existente == null) throw new EntidadNoEncontradaException("Usuario inexistente: " + cmd.getIdUsuario());

        // Si es lector puro (solo rol LECTOR) validar que no tenga préstamos de ningún estado
        boolean esLector = existente.getRoles().contains(Rol.LECTOR);
        if (esLector) {
            Integer lectorId = existente.getLectorId();
            if (lectorId != null) {
                var prestamos = prestamoRepo.todosLosPorLector(lectorId);
                if (!prestamos.isEmpty()) {
                    throw new ReglaDeNegocioException("No se puede eliminar: el lector tiene préstamos asociados");
                }
            }
        }

        usuarioRepo.eliminar(cmd.getIdUsuario());
    }
}
