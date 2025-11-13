package biblioteca.biblioteca.application.command;

import biblioteca.biblioteca.domain.exception.DatoInvalidoException;
import biblioteca.biblioteca.domain.model.Lector;
import biblioteca.biblioteca.domain.port.ILectorRepository;
import biblioteca.biblioteca.web.dto.LectorDto;
import biblioteca.biblioteca.web.mapper.LectorDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CrearLectorCommandHandler {

    private final ILectorRepository lectorRepo;
    private final LectorDtoMapper dtoMapper;

    @Transactional
    public LectorDto handle(CrearLectorCommand cmd) {
        if (cmd == null) throw new DatoInvalidoException("El comando no puede ser null");

        // Asumimos que el ID autoincremental lo asigna la infraestructura al guardar.
        // Creamos el Lector con un id "provisorio" (null) o con un generador si tu dominio lo exige.
        // Si tu Lector requiere id no-null en constructor, podés usar un factory en infra.
        Lector lector = Lector.nuevo(cmd.getNombre()); // placeholder id; será reemplazado en persistencia
        lectorRepo.guardar(lector);

        // Volvemos a leer para reflejar ID asignado, si tu adapter lo requiere.
        // Alternativa: adaptar guardar(...) para que retorne el Lector con ID; aquí lo simplificamos.
        Lector creado = lectorRepo.porId(lector.getIdLector());

        return dtoMapper.toDto(creado != null ? creado : lector);
    }
}
