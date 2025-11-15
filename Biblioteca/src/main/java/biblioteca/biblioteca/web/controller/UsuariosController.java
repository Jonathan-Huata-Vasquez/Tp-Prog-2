package biblioteca.biblioteca.web.controller;

import biblioteca.biblioteca.application.command.*;
import biblioteca.biblioteca.web.dto.UsuarioDto;
import biblioteca.biblioteca.application.query.ListarUsuariosQuery;
import biblioteca.biblioteca.application.query.ObtenerUsuarioQuery;
import biblioteca.biblioteca.application.query.ListarUsuariosQueryHandler;
import biblioteca.biblioteca.application.query.ObtenerUsuarioQueryHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuariosController {

    private final CrearUsuarioCommandHandler crearHandler;
    private final ActualizarUsuarioCommandHandler actualizarHandler;
    private final ActualizarPasswordCommandHandler actualizarPasswordHandler;
    private final EliminarUsuarioCommandHandler eliminarHandler;
    private final ObtenerUsuarioQueryHandler obtenerHandler;
    private final ListarUsuariosQueryHandler listarHandler;

    @PostMapping
    public ResponseEntity<UsuarioDto> crear(@Valid @RequestBody CrearUsuarioCommand body) {
        var dto = crearHandler.handle(body);
        return ResponseEntity.created(URI.create("/api/usuarios/" + dto.getId())).body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDto> actualizar(@PathVariable Integer id,
                                                 @Valid @RequestBody ActualizarUsuarioCommand body) {
        var dto = actualizarHandler.handle(
                ActualizarUsuarioCommand.builder()
                        .idUsuario(id) // el path domina
                        .nombreCompleto(body.getNombreCompleto())
                        .email(body.getEmail())
                        .roles(body.getRoles())
                        .lectorId(body.getLectorId())
                        .build()
        );
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<Void> actualizarPassword(@PathVariable Integer id,
                                                   @Valid @RequestBody ActualizarPasswordCommand body) {
        actualizarPasswordHandler.handle(
                ActualizarPasswordCommand.builder()
                        .idUsuario(id) // path domina
                        .passwordHash(body.getPasswordHash())
                        .build()
        );
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        eliminarHandler.handle(EliminarUsuarioCommand.builder().idUsuario(id).build());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDto> porId(@PathVariable Integer id) {
        var dto = obtenerHandler.handle(ObtenerUsuarioQuery.builder().idUsuario(id).build());
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDto>> todos() {
        var lista = listarHandler.handle(ListarUsuariosQuery.builder().build());
        return ResponseEntity.ok(lista);
    }
}
