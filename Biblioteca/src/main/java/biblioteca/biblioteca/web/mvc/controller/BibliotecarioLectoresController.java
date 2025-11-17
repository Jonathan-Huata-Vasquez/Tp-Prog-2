package biblioteca.biblioteca.web.mvc.controller;

import biblioteca.biblioteca.application.query.ListarLectoresQuery;
import biblioteca.biblioteca.application.query.ListarLectoresQueryHandler;
import biblioteca.biblioteca.infrastructure.security.UsuarioDetalles;
import biblioteca.biblioteca.web.dto.ListarLectoresResultDto;
import biblioteca.biblioteca.web.helper.ControllerHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

/**
 * Controller para gestión de lectores por bibliotecarios.
 */
@Controller
@RequestMapping("/bibliotecario")
@RequiredArgsConstructor
@Slf4j
public class BibliotecarioLectoresController {

    private final ControllerHelper controllerHelper;
    private final ListarLectoresQueryHandler listarLectoresQueryHandler;

    @GetMapping("/lectores")
    public String lectores(@AuthenticationPrincipal UsuarioDetalles usuario,
                          HttpSession session,
                          @RequestParam(value = "estado", required = false) String estadoFiltro,
                          @RequestParam(value = "pagina", defaultValue = "0") int pagina,
                          @RequestParam(value = "tamano", defaultValue = "20") int tamano,
                          Model model) {

        log.debug("Cargando página de lectores para bibliotecario: {}, filtro: {}, página: {}, tamaño: {}",
                  usuario != null ? usuario.getNombreCompleto() : "Anónimo", estadoFiltro, pagina, tamano);

        try {
            // Crear query
            ListarLectoresQuery query = ListarLectoresQuery.builder()
                    .estadoFiltro(estadoFiltro)
                    .pagina(pagina)
                    .tamano(tamano)
                    .build();
            
            // Ejecutar query
            ListarLectoresResultDto resultado = listarLectoresQueryHandler.handle(query);
            
            // Agregar datos al modelo
            model.addAttribute("paginaLectores", resultado.getPaginaLectores());
            model.addAttribute("resumen", resultado.getResumen());
            model.addAttribute("estadoFiltro", estadoFiltro);
            
            // Agregar rol actual para navbar
            controllerHelper.agregarRolActualAlModelo(model, usuario, session);

            log.debug("Página de lectores cargada exitosamente con {} lectores", 
                     resultado.getPaginaLectores().getContenido().size());
            return "bibliotecario/lectores";

        } catch (Exception e) {
            log.error("Error al cargar página de lectores", e);
            model.addAttribute("error", "Error al cargar la página de lectores");
            return "error";
        }
    }
}