package biblioteca.biblioteca.web.mvc.controller;


import biblioteca.biblioteca.application.query.ListarCopiasQuery;
import biblioteca.biblioteca.application.query.ListarCopiasQueryHandler;
import biblioteca.biblioteca.infrastructure.security.UsuarioDetalles;
import biblioteca.biblioteca.web.helper.ControllerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class AdminCopiasController {

    private final ControllerHelper controllerHelper;
    private final ListarCopiasQueryHandler listarCopiasQueryHandler;

    @GetMapping("/admin/copias")
    public String listar(@AuthenticationPrincipal UsuarioDetalles usuario,
                         HttpSession session,
                         Model model) {
        var result = listarCopiasQueryHandler.handle(new ListarCopiasQuery());
        model.addAttribute("copias", result);
        model.addAttribute("admin", usuario);
        model.addAttribute("adminIniciales", controllerHelper.calcularIniciales(usuario));
        controllerHelper.agregarRolActualAlModelo(model, usuario, session);
        return "admin/admin-copias";
    }
}
