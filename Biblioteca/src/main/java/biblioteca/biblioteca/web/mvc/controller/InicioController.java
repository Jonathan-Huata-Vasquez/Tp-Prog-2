package biblioteca.biblioteca.web.mvc.controller;

import biblioteca.biblioteca.infrastructure.security.UsuarioDetalles;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Set;

@Controller
public class InicioController {

    @GetMapping("/")
    public String home(@AuthenticationPrincipal UsuarioDetalles usuario) {
        if (usuario == null) {
            return "redirect:/login";
        }
        Set<String> roles = usuario.getAuthorities().stream()
                .map(a -> a.getAuthority()) // "ROLE_LECTOR", etc.
                .collect(java.util.stream.Collectors.toSet());

        if (roles.size() == 1) {
            if (roles.contains("ROLE_LECTOR")) return "redirect:/dashboard/lector";
            if (roles.contains("ROLE_BIBLIOTECARIO")) return "redirect:/dashboard/bibliotecario";
            if (roles.contains("ROLE_ADMINISTRADOR")) return "redirect:/dashboard/admin";
        }
        // Más de un rol → ir a selector
        return "redirect:/seleccionar-rol";
    }
}
