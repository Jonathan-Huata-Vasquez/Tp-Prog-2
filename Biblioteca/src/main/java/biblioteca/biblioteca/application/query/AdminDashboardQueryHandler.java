package biblioteca.biblioteca.application.query;

import biblioteca.biblioteca.web.dto.AdminDashboardDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminDashboardQueryHandler {

    private final IBibliotecaQueriesRepository queriesRepository;

    public AdminDashboardDto handle(AdminDashboardQuery query) {
        AdminDashboardDto dto = queriesRepository.obtenerResumenAdmin();
        log.debug("Dashboard admin: usuarios={}, libros={}, copias={}", dto.getUsuariosTotal(), dto.getLibrosTotal(), dto.getCopiasTotal());
        return dto;
    }
}
