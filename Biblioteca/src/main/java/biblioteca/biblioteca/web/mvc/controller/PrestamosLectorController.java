package biblioteca.biblioteca.web.mvc.controller;

import biblioteca.biblioteca.application.query.PrestamosLectorQuery;
import biblioteca.biblioteca.application.query.PrestamosLectorQueryHandler;
import biblioteca.biblioteca.infrastructure.security.UsuarioDetalles;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class PrestamosLectorController {

    private final PrestamosLectorQueryHandler prestamosLectorQueryHandler;

    @GetMapping("/lector/prestamos")
    public String prestamos(@AuthenticationPrincipal UsuarioDetalles usuario, Model model) {

        if (usuario.getLectorId() == null) {
            // Si no tiene lectorId, mostrar vista vacía
            model.addAttribute("resumen", null);
            model.addAttribute("prestamos", null);
            return "lector/prestamos";
        }

        // Construir la query
        PrestamosLectorQuery query = PrestamosLectorQuery.builder()
                .idLector(usuario.getLectorId())
                .build();

        // Ejecutar la query usando el handler
        var resultado = prestamosLectorQueryHandler.handle(query);

        // Pasar los DTOs al modelo de Thymeleaf
        model.addAttribute("resumen", resultado.getResumen());
        model.addAttribute("prestamos", resultado.getPrestamos());

        return "lector/prestamos";
    }
}
