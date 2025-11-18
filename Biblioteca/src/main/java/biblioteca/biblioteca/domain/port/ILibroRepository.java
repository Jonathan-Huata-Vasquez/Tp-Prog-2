package biblioteca.biblioteca.domain.port;

import biblioteca.biblioteca.domain.model.Libro;

import java.util.List;

public interface ILibroRepository {
    Libro porId(Integer id);
    List<Libro> todos();
    List<Libro> buscarPorTitulo(String q);
    void eliminar(Integer idLibro);
    Libro guardar(Libro libro);
    int contarCopiasPorLibro(Integer idLibro);
}
