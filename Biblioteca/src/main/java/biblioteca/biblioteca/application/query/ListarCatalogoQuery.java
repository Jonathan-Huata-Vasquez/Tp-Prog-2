package biblioteca.biblioteca.application.query;

import lombok.Value;

@Value
public class ListarCatalogoQuery {
    String q; // puede ser null o vacío → trae todos
}
