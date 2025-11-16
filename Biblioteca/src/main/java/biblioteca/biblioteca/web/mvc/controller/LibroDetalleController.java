package biblioteca.biblioteca.web.mvc.controller;

import biblioteca.biblioteca.application.query.DetalleLibroQuery;
import biblioteca.biblioteca.application.query.DetalleLibroQueryHandler;
import biblioteca.biblioteca.infrastructure.security.UsuarioDetalles;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class LibroDetalleController {

    private final DetalleLibroQueryHandler detalleLibroQueryHandler;

    @GetMapping("/catalogo/libro/{id}")
    public String detalleLibro(@PathVariable("id") Integer id, Model model) {
        
        // Obtener el ID del lector actual desde el contexto de seguridad
        Integer idLectorActual = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UsuarioDetalles) {
            UsuarioDetalles usuarioDetalles = (UsuarioDetalles) auth.getPrincipal();
            idLectorActual = usuarioDetalles.getLectorId();
        }

        // Construir la query
        DetalleLibroQuery query = DetalleLibroQuery.builder()
                .idLibro(id)
                .idLectorActual(idLectorActual)
                .build();

        // Ejecutar la query usando el handler
        var libro = detalleLibroQueryHandler.handle(query);

        // Pasar el DTO al modelo de Thymeleaf
        model.addAttribute("libro", libro);

        return "lector/detalle-libro";
    }
}