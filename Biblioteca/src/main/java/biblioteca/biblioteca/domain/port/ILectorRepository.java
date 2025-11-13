package biblioteca.biblioteca.domain.port;


import biblioteca.biblioteca.domain.model.Lector;

import java.time.LocalDate;
import java.util.List;

public interface ILectorRepository {
    void guardar(Lector lector);
    Lector porId(Integer idLector);
    List<Lector> lectoresBloqueados(LocalDate hastaFecha);
}
