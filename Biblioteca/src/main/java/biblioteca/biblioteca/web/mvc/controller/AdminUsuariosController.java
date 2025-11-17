package biblioteca.biblioteca.web.mvc.controller;

import biblioteca.biblioteca.application.query.ListarUsuariosQuery;
import biblioteca.biblioteca.application.query.ListarUsuariosQueryHandler;
import biblioteca.biblioteca.infrastructure.security.UsuarioDetalles;
import biblioteca.biblioteca.web.helper.ControllerHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AdminUsuariosController {

    private final ControllerHelper controllerHelper;
    private final ListarUsuariosQueryHandler listarUsuariosQueryHandler;

    @GetMapping("/admin/usuarios")
    public String listar(@AuthenticationPrincipal UsuarioDetalles usuario,
                         HttpSession session,
                         Model model) {
        log.debug("Vista admin usuarios solicitada por: {}", usuario != null ? usuario.getUsername() : "anon");

        // Datos para la tabla
        var usuarios = listarUsuariosQueryHandler.handle(ListarUsuariosQuery.builder().build());
        model.addAttribute("usuarios", usuarios);

        // Datos comunes para navbar
        model.addAttribute("admin", usuario);
        model.addAttribute("adminIniciales", calcularIniciales(usuario));
        controllerHelper.agregarRolActualAlModelo(model, usuario, session);

        return "admin/admin-usuarios";
    }

    @GetMapping("/admin/usuarios/nuevo")
    public String nuevo(@AuthenticationPrincipal UsuarioDetalles usuario,
                        HttpSession session,
                        Model model) {
        log.debug("Formulario nuevo usuario (admin) solicitado por: {}", usuario != null ? usuario.getUsername() : "anon");

        model.addAttribute("admin", usuario);
        model.addAttribute("adminIniciales", calcularIniciales(usuario));
        controllerHelper.agregarRolActualAlModelo(model, usuario, session);
        // Aquí podría agregarse un objeto command/dto vacío para el form si se define.
        return "admin/admin-usuario-form";
    }

    private String calcularIniciales(UsuarioDetalles usuario) {
        String iniciales = "AD"; // fallback genérico
        if (usuario != null && usuario.getNombreCompleto() != null) {
            String[] partes = usuario.getNombreCompleto().trim().split("\\s+");
            if (partes.length >= 1) {
                iniciales = partes[0].substring(0, 1).toUpperCase();
                if (partes.length >= 2) {
                    iniciales += partes[1].substring(0, 1).toUpperCase();
                }
            }
        }
        return iniciales;
    }
}
