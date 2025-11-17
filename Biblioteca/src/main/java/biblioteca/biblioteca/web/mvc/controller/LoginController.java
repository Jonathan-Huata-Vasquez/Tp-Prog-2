package biblioteca.biblioteca.web.mvc.controller;

import biblioteca.biblioteca.infrastructure.security.UsuarioDetalles;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginPage(@AuthenticationPrincipal UsuarioDetalles usuario) {
        if (usuario != null) {
            return "redirect:/";
        }
        return "login"; // templates/login.html
    }
}
