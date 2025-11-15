package biblioteca.biblioteca.infrastructure.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

public class RolRedirectAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        Authentication auth = (Authentication) request.getUserPrincipal();

        if (auth == null || !auth.isAuthenticated()) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Set<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        if (roles.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (roles.size() > 1) {
            response.sendRedirect(request.getContextPath() + "/seleccionar-rol");
            return;
        }

        // Un solo rol → redirigimos al dashboard de ese rol
        String rol = roles.iterator().next();
        String destino = switch (rol) {
            case "ROLE_ADMINISTRADOR" -> "/dashboard/admin";
            case "ROLE_BIBLIOTECARIO" -> "/dashboard/bibliotecario";
            case "ROLE_LECTOR" -> "/dashboard/lector";
            default -> "/"; // fallback seguro
        };
        response.sendRedirect(request.getContextPath() + destino);
    }
}
