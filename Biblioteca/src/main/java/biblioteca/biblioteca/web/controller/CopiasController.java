package biblioteca.biblioteca.web.controller;

import biblioteca.biblioteca.application.command.ActualizarCopiaCommand;
import biblioteca.biblioteca.application.command.CrearCopiaCommand;
import biblioteca.biblioteca.application.command.EliminarCopiaCommand;
import biblioteca.biblioteca.application.command.ActualizarCopiaCommandHandler;
import biblioteca.biblioteca.application.command.CrearCopiaCommandHandler;
import biblioteca.biblioteca.application.command.EliminarCopiaCommandHandler;
import biblioteca.biblioteca.application.query.ListarCopiasQuery;
import biblioteca.biblioteca.application.query.ObtenerCopiaQuery;
import biblioteca.biblioteca.application.query.ListarCopiasQueryHandler;
import biblioteca.biblioteca.application.query.ObtenerCopiaQueryHandler;
import biblioteca.biblioteca.web.dto.CopiaDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/copias")
@RequiredArgsConstructor
public class CopiasController {

    private final CrearCopiaCommandHandler crearHandler;
    private final ActualizarCopiaCommandHandler actualizarHandler;
    private final EliminarCopiaCommandHandler eliminarHandler;
    private final ObtenerCopiaQueryHandler obtenerHandler;
    private final ListarCopiasQueryHandler listarHandler;

    @PostMapping
    public ResponseEntity<CopiaDto> crear(@Valid @RequestBody CrearCopiaCommand body) {
        var dto = crearHandler.handle(body);
        return ResponseEntity
                .created(URI.create("/api/copias/" + dto.getId()))
                .body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CopiaDto> actualizar(@PathVariable Integer id,
                                               @Valid @RequestBody ActualizarCopiaCommand body) {
        var dto = actualizarHandler.handle(
                ActualizarCopiaCommand.builder()
                        .idCopia(id) // el path domina
                        .nuevoEstado(body.getNuevoEstado())
                        .build()
        );
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        eliminarHandler.handle(EliminarCopiaCommand.builder().idCopia(id).build());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CopiaDto> porId(@PathVariable Integer id) {
        var dto = obtenerHandler.handle(ObtenerCopiaQuery.builder().idCopia(id).build());
        return ResponseEntity.ok(dto);
    }


}
