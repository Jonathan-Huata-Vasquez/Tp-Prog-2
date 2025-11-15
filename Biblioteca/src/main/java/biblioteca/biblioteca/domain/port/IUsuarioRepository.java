package biblioteca.biblioteca.domain.port;

import biblioteca.biblioteca.domain.model.Usuario;

import java.util.List;

public interface IUsuarioRepository {
    Usuario guardar(Usuario usuario);  // retorna con ID si fue INSERT
    Usuario porId(Integer id);
    Usuario porEmail(String email);
    Usuario porDni(String dni);
    void eliminar(Integer id);
    List<Usuario> todos();
}

