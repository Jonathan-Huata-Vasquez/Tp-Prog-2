package biblioteca.biblioteca.web.mvc.controller;

import biblioteca.biblioteca.application.command.ListarCatalogoCommand;
import biblioteca.biblioteca.application.command.ListarCatalogoCommandHandler;
import biblioteca.biblioteca.infrastructure.security.UsuarioDetalles;
import biblioteca.biblioteca.web.helper.ControllerHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CatalogoController {

    private final ListarCatalogoCommandHandler listarHandler;
    private final ControllerHelper controllerHelper;

    @GetMapping({"/catalogo", "/lector/catalogo", "/bibliotecario/catalogo", "/catalogo/buscar"})
    public String catalogo(@RequestParam(value = "q", required = false) String q, 
                          @AuthenticationPrincipal UsuarioDetalles usuario,
                          HttpSession session,
                          Model model) {
        log.debug("CatalogoController.catalogo() llamado para usuario: {}, query: {}", 
                 usuario != null ? usuario.getUsername() : "null", q);
        
        var result = listarHandler.handle(new ListarCatalogoCommand(q));
        model.addAttribute("totalLibros", result.getTotalLibros());
        model.addAttribute("criterioBusqueda", result.getCriterio());
        model.addAttribute("libros", result.getItems()); // lista de LibroCatalogoItemDto
        
        // Agregar rol actual para mostrar navbar correcto
        controllerHelper.agregarRolActualAlModelo(model, usuario, session);
        
        // Debug: verificar qué rol se está pasando
        String rolActual = controllerHelper.obtenerRolActual(usuario, session);
        log.debug("CatalogoController: rol determinado = '{}'", rolActual);
        
        log.debug("CatalogoController: retornando vista shared/catalogo");
        return "shared/catalogo";
    }
}
