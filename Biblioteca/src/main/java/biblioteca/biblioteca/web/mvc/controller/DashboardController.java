package biblioteca.biblioteca.web.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard/lector")
    public String vistaLector() {
        return "dashboard-lector"; // templates/dashboard-lector.html
    }

    @GetMapping("/dashboard/bibliotecario")
    public String vistaBibliotecario() {
        return "dashboard-bibliotecario"; // templates/dashboard-bibliotecario.html
    }

    @GetMapping("/dashboard/admin")
    public String vistaAdmin() {
        return "dashboard-admin"; // templates/dashboard-admin.html
    }
}
