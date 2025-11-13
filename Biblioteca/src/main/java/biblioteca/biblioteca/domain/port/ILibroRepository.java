package biblioteca.biblioteca.domain.port;

import biblioteca.biblioteca.domain.model.Libro;

import java.util.List;

public interface ILibroRepository {
    Libro guardar(Libro libro);
    Libro porId(Integer idLibro);
    List<Libro> buscarPorAutor(Integer idAutor);
    void eliminar(Integer idLibro);
    List<Libro> todos();
}
