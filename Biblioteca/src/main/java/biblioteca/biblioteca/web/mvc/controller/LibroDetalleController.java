package biblioteca.biblioteca.web.mvc.controller;

import biblioteca.biblioteca.application.query.DetalleLibroQuery;
import biblioteca.biblioteca.application.query.DetalleLibroQueryHandler;
import biblioteca.biblioteca.infrastructure.security.UsuarioDetalles;
import biblioteca.biblioteca.web.helper.ControllerHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class LibroDetalleController {

    private final DetalleLibroQueryHandler detalleLibroQueryHandler;
    private final ControllerHelper controllerHelper;

    @GetMapping("/libro/{id}")
    public String detalleLibro(@PathVariable("id") Integer id, 
                              @AuthenticationPrincipal UsuarioDetalles usuario,
                              HttpSession session,
                              Model model) {
        
        // Obtener el ID del lector actual desde el usuario autenticado
        Integer idLectorActual = (usuario != null) ? usuario.getLectorId() : null;

        // Construir la query
        DetalleLibroQuery query = DetalleLibroQuery.builder()
                .idLibro(id)
                .idLectorActual(idLectorActual)
                .build();

        // Ejecutar la query usando el handler
        var libro = detalleLibroQueryHandler.handle(query);

        // Pasar el DTO al modelo de Thymeleaf
        model.addAttribute("libro", libro);
        
        // Agregar rol actual para mostrar navbar correcto
        controllerHelper.agregarRolActualAlModelo(model, usuario, session);

        return "shared/detalle-libro";
    }

    // Alias para acceder al detalle desde el catálogo (/catalogo/libro/{id})
    @GetMapping("/catalogo/libro/{id}")
    public String detalleLibroDesdeCatalogo(@PathVariable("id") Integer id,
                                            @AuthenticationPrincipal UsuarioDetalles usuario,
                                            HttpSession session,
                                            Model model) {
        return detalleLibro(id, usuario, session, model);
    }
}