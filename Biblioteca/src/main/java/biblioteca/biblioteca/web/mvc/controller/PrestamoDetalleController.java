package biblioteca.biblioteca.web.mvc.controller;

import biblioteca.biblioteca.application.query.PrestamoDetalleQuery;
import biblioteca.biblioteca.application.query.PrestamoDetalleQueryHandler;
import biblioteca.biblioteca.infrastructure.security.UsuarioDetalles;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class PrestamoDetalleController {

    private final PrestamoDetalleQueryHandler prestamoDetalleQueryHandler;

    @GetMapping("/prestamos/{id}")
    public String detalle(@PathVariable("id") Integer id, 
                         @AuthenticationPrincipal UsuarioDetalles usuario,
                         Model model) {

        // Construir la query
        PrestamoDetalleQuery query = PrestamoDetalleQuery.builder()
                .idPrestamo(id)
                .build();

        // Ejecutar la query usando el handler
        var prestamo = prestamoDetalleQueryHandler.handle(query);

        // Determinar rol actual para navegación
        String rolActual = determinarRolActual(usuario);

        model.addAttribute("prestamo", prestamo);
        model.addAttribute("rolActual", rolActual);

        return "prestamos/detalle-prestamo";
    }

    private String determinarRolActual(UsuarioDetalles usuario) {
        if (usuario != null && usuario.getLectorId() != null) {
            return "LECTOR";
        }
        return "BIBLIOTECARIO"; // Por defecto si no tiene lectorId
    }
}
