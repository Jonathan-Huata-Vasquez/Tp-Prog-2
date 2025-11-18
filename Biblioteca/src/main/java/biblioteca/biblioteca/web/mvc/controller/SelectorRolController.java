package biblioteca.biblioteca.web.mvc.controller;

import biblioteca.biblioteca.application.service.RolActualService;
import biblioteca.biblioteca.infrastructure.security.UsuarioDetalles;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SelectorRolController {

    private final RolActualService rolActualService;
    
    private static final String REDIRECT_LOGIN = "redirect:/login";

    @GetMapping("/seleccionar-rol")
    public String selector(@AuthenticationPrincipal UsuarioDetalles usuario, 
                          HttpSession session, Model model) {
        if (usuario == null) return REDIRECT_LOGIN;

        // Verificar si tiene múltiples roles
        if (!rolActualService.tieneMultiplesRoles(usuario)) {
            // Un solo rol, redirigir directamente
            String rolActual = rolActualService.determinarRolActivo(usuario, session);
            return redirigirPorRol("ROLE_" + rolActual);
        }
        log.info("usuario.getAuthorities() {}", usuario.getAuthorities());
        // Múltiples roles, mostrar selector
        model.addAttribute("nombre", usuario.getNombreCompleto());
        model.addAttribute("roles", usuario.getAuthorities());
        return "selector-rol";
    }

    @PostMapping("/seleccionar-rol")
    public String seleccionar(@AuthenticationPrincipal UsuarioDetalles usuario,
                              @RequestParam("rol") String rol,
                              HttpSession session) {
        if (usuario == null) return REDIRECT_LOGIN;

        // Establecer el rol seleccionado en la sesión
        String rolSinPrefix = rol.replace("ROLE_", "");
        rolActualService.establecerRolActivo(rolSinPrefix, session);
        
        // Redirigir según el rol seleccionado
        return redirigirPorRol(rol);
    }

    private String redirigirPorRol(String rol) {
        return switch (rol) {
            case "ROLE_ADMINISTRADOR"  -> "redirect:/dashboard/admin";
            case "ROLE_BIBLIOTECARIO"  -> "redirect:/dashboard/bibliotecario";
            case "ROLE_LECTOR"         -> "redirect:/dashboard/lector";
            default                    -> "redirect:/";
        };
    }
}
