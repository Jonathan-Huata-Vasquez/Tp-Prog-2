package biblioteca.biblioteca.web.mvc.controller;

import biblioteca.biblioteca.application.query.ListarLibrosQuery;
import biblioteca.biblioteca.application.query.ListarLibrosQueryHandler;
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
public class AdminLibrosController {

    private final ControllerHelper controllerHelper;
    private final ListarLibrosQueryHandler listarLibrosQueryHandler;

    @GetMapping("/admin/libros")
    public String listar(@AuthenticationPrincipal UsuarioDetalles usuario,
                         HttpSession session,
                         Model model) {
        log.debug("Vista admin libros solicitada por: {}", usuario != null ? usuario.getUsername() : "anon");

        var libros = listarLibrosQueryHandler.handle(ListarLibrosQuery.builder().build());
        model.addAttribute("libros", libros);

        model.addAttribute("admin", usuario);
        model.addAttribute("adminIniciales", calcularIniciales(usuario));
        controllerHelper.agregarRolActualAlModelo(model, usuario, session);

        return "admin/admin-libros";
    }

    @GetMapping("/admin/libros/nuevo")
    public String nuevo(@AuthenticationPrincipal UsuarioDetalles usuario,
                        HttpSession session,
                        Model model) {
        log.debug("Formulario nuevo libro (admin) solicitado por: {}", usuario != null ? usuario.getUsername() : "anon");

        model.addAttribute("admin", usuario);
        model.addAttribute("adminIniciales", calcularIniciales(usuario));
        controllerHelper.agregarRolActualAlModelo(model, usuario, session);
        // Se podría añadir un DTO/command vacío para el formulario si corresponde.
        return "admin/admin-libro-form"; // Asegurarse de crear template si no existe.
    }

    private String calcularIniciales(UsuarioDetalles usuario) {
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
        return iniciales;
    }
}
