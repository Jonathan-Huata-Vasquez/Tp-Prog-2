package biblioteca.biblioteca.domain.port;

import biblioteca.biblioteca.domain.model.Autor;

import java.util.List;

public interface IAutorRepository {
    Autor guardar(Autor autor);
    Autor porId(Integer idAutor);
    void eliminar(Integer idAutor);
    List<Autor> todos();
}
