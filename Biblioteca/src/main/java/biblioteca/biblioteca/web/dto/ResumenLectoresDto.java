package biblioteca.biblioteca.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * DTO para resumen de lectores en el dashboard.
 */
@Getter
@Builder
@RequiredArgsConstructor
public class ResumenLectoresDto {
    private final int totalLectores;
    private final int habilitados;
    private final int bloqueados;
}