package biblioteca.biblioteca.web.mvc.controller;

import biblioteca.biblioteca.application.query.LectorDashboardQuery;
import biblioteca.biblioteca.application.query.LectorDashboardQueryHandler;
import biblioteca.biblioteca.infrastructure.security.UsuarioDetalles;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controllers MVC (Thymeleaf) para dashboards.
 */
@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final LectorDashboardQueryHandler lectorDashboardQueryHandler;

    @GetMapping("/dashboard/lector")
    public String vistaLector(@AuthenticationPrincipal UsuarioDetalles usuario, Model model) {
        
        if (usuario.getLectorId() == null) {
            // Si no tiene lectorId, mostrar vista con datos vacíos
            model.addAttribute("dashboard", null);
            return "lector/dashboard";
        }

        // Construir la query
        LectorDashboardQuery query = LectorDashboardQuery.builder()
                .idLector(usuario.getLectorId())
                .build();

        // Ejecutar la query usando el handler
        var dashboard = lectorDashboardQueryHandler.handle(query);

        model.addAttribute("dashboard", dashboard);
        // Mantener compatibilidad con template existente
        model.addAttribute("bloqueado", dashboard.isBloqueado());
        model.addAttribute("bloqueadoHasta", dashboard.getBloqueadoHasta());
        model.addAttribute("prestamosActivos", dashboard.getPrestamosActivos());
        model.addAttribute("proximoVencimiento", dashboard.getProximoVencimiento());

        return "lector/dashboard";
    }

    // Los otros dashboards pueden seguir como estaban:
    @GetMapping("/dashboard/bibliotecario")
    public String vistaBibliotecario() { 
        return "dashboard-bibliotecario"; 
    }

    @GetMapping("/dashboard/admin")
    public String vistaAdmin() { 
        return "dashboard-admin"; 
    }

}



