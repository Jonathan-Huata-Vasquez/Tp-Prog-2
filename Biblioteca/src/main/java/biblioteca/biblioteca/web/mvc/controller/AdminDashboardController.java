package biblioteca.biblioteca.web.mvc.controller;

import biblioteca.biblioteca.infrastructure.security.UsuarioDetalles;
import biblioteca.biblioteca.web.helper.ControllerHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import biblioteca.biblioteca.application.query.AdminDashboardQuery;
import biblioteca.biblioteca.application.query.AdminDashboardQueryHandler;
import biblioteca.biblioteca.web.dto.AdminDashboardDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;

/**
 * Controller específico para dashboard del administrador.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class AdminDashboardController {

    private final ControllerHelper controllerHelper;

    private final AdminDashboardQueryHandler adminDashboardQueryHandler;

    @GetMapping("/dashboard/admin")
    public String dashboard(@AuthenticationPrincipal UsuarioDetalles usuario,
                            HttpSession session,
                            Model model) {

        log.debug("Cargando dashboard administrador para usuario: {}", usuario != null ? usuario.getNombreCompleto() : "Anónimo");

        // Obtener métricas reales vía QueryHandler
        AdminDashboardDto resumen = adminDashboardQueryHandler.handle(new AdminDashboardQuery());
        model.addAttribute("resumen", resumen);

        // Atributo esperado por fragment navbar-admin
        model.addAttribute("admin", usuario);

        // Iniciales seguras para avatar (primeras letras de hasta dos palabras)
        String iniciales = "AD";
        if (usuario != null && usuario.getNombreCompleto() != null) {
            String[] partes = usuario.getNombreCompleto().trim().split("\\s+");
            if (partes.length >= 1) {
                iniciales = partes[0].substring(0,1).toUpperCase();
                if (partes.length >= 2) {
                    iniciales += partes[1].substring(0,1).toUpperCase();
                }
            }
        }
        model.addAttribute("adminIniciales", iniciales);

        // Rol actual para navbar dinámico si se usa shared views
        controllerHelper.agregarRolActualAlModelo(model, usuario, session);

        return "admin/dashboard-admin"; // Ubicación real del template
    }
}