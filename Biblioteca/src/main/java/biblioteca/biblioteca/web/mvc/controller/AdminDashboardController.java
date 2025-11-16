package biblioteca.biblioteca.web.mvc.controller;

import biblioteca.biblioteca.infrastructure.security.UsuarioDetalles;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller específico para dashboard del administrador.
 */
@Controller
@RequiredArgsConstructor
public class AdminDashboardController {

    // TODO: Agregar QueryHandler cuando se implemente funcionalidad
    // private final AdminDashboardQueryHandler adminDashboardQueryHandler;

    @GetMapping("/dashboard/admin")
    public String dashboard(@AuthenticationPrincipal UsuarioDetalles usuario, Model model) {
        
        // TODO: Implementar métricas específicas para administrador
        // Por ahora solo datos placeholder
        model.addAttribute("usuario", usuario);

        return "dashboard-admin";
    }
}