package biblioteca.biblioteca.application.command;

import lombok.Value;

@Value
public class ListarCatalogoCommand {
    String q; // puede ser null o vacío → trae todos
}
