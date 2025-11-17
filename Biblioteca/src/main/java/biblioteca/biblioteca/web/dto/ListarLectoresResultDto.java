package biblioteca.biblioteca.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;


@Getter
@Builder
@RequiredArgsConstructor
public class ListarLectoresResultDto {
    private final PaginaDto<LectorBibliotecarioDto> paginaLectores;
    private final ResumenLectoresDto resumen;
}