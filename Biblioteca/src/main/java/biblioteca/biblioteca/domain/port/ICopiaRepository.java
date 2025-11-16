package biblioteca.biblioteca.domain.port;

import biblioteca.biblioteca.domain.model.Copia;
import biblioteca.biblioteca.domain.model.EstadoCopia;

import java.util.List;

public interface ICopiaRepository {
    Copia guardar(Copia copia);            // retorna con ID si fue INSERT
    Copia porId(Integer idCopia);
    List<Copia> disponiblesPorLibro(Integer idLibro);
    void eliminar(Integer idCopia);
    List<Copia> todas();
    List<Copia> porLibro(Integer idLibro);
    int contarPorLibro(Integer idLibro);
    int contarPorLibroYEstado(Integer idLibro, EstadoCopia estado);
}
