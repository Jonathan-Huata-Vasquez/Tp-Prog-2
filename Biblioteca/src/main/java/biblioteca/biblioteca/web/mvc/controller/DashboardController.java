package biblioteca.biblioteca.web.mvc.controller;

import biblioteca.biblioteca.infrastructure.security.UsuarioDetalles;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controllers MVC (Thymeleaf) para dashboards.
 * Nota: este método reemplaza el anterior que devolvía "dashboard-lector".
 */
@Controller
@RequiredArgsConstructor
public class DashboardController {

    // Si luego querés traer datos reales, podés inyectar servicios de consulta aquí
    // private final ILectorResumenQuery lectorResumenQuery;

    @GetMapping("/dashboard/lector")
    public String vistaLector(@AuthenticationPrincipal UsuarioDetalles usuario, Model model) {
        // Datos mínimos para la vista (placeholder hasta conectar queries reales)
        model.addAttribute("bloqueado", false);
        model.addAttribute("bloqueadoHasta", null);
        model.addAttribute("prestamosActivos", 0);
        model.addAttribute("proximoVencimiento", null);

        // Ejemplo con servicios reales:
        // var resumen = lectorResumenQuery.obtenerResumen(usuario.getLectorId());
        // model.addAttribute("bloqueado", resumen.isBloqueado());
        // model.addAttribute("bloqueadoHasta", resumen.getBloqueadoHasta());
        // model.addAttribute("prestamosActivos", resumen.getPrestamosActivos());
        // model.addAttribute("proximoVencimiento", resumen.getProximoVencimiento());

        return "lector/dashboard"; // <-- NUEVA vista
    }

    // Los otros dashboards pueden seguir como estaban:
    @GetMapping("/dashboard/bibliotecario")
    public String vistaBibliotecario() { return "dashboard-bibliotecario"; }

    @GetMapping("/dashboard/admin")
    public String vistaAdmin() { return "dashboard-admin"; }
}
