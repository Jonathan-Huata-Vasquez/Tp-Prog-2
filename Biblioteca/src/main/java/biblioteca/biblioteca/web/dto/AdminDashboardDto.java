package biblioteca.biblioteca.web.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AdminDashboardDto {
    int usuariosTotal;
    int librosTotal;
    int copiasTotal;
    int autoresTotal;
    int editorialesTotal;
    int usuariosAdmin;        // desglose por rol
    int usuariosBibliotecario;
    int usuariosLector;
}
