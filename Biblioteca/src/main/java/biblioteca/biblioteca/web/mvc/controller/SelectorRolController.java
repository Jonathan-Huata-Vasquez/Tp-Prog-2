package biblioteca.biblioteca.web.mvc.controller;

import biblioteca.biblioteca.infrastructure.security.UsuarioDetalles;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class SelectorRolController {

    @GetMapping("/seleccionar-rol")
    public String selector(@AuthenticationPrincipal UsuarioDetalles usuario, Model model) {
        if (usuario == null) return "redirect:/login";

        Set<String> roles = usuario.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority) // "ROLE_LECTOR"...
                .collect(Collectors.toSet());

        // Si tiene 0 (raro) o 1 rol → redirigir a destino correspondiente
        if (roles.isEmpty()) return "redirect:/login";
        if (roles.size() == 1) return redirigirPorRol(roles.iterator().next());

        // > 1 rol → mostrar selector
        model.addAttribute("nombre", usuario.getNombreCompleto());
        model.addAttribute("roles", usuario.getAuthorities());
        return "selector-rol";
    }

    @PostMapping("/seleccionar-rol")
    public String seleccionar(@AuthenticationPrincipal UsuarioDetalles usuario,
                              @RequestParam("rol") String rol) {
        if (usuario == null) return "redirect:/login";

        // Si el usuario tiene solo 1 rol, no debería llegar aquí: redirigir a su dashboard
        long count = usuario.getAuthorities().size();
        if (count <= 1) {
            var unico = usuario.getAuthorities().iterator().next().getAuthority();
            return redirigirPorRol(unico);
        }

        // Usuario multi-rol: usar el elegido
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
