package biblioteca.biblioteca.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor // genera ctor privado para el campo final 'label'
public enum Categoria {
    Pedagogia("Pedagogía"),
    Novela("Novela"),
    Teatro("Teatro"),
    Poesia("Poesía"),
    Ensayo("Ensayo");

    private final String label; // texto amigable para mostrar
}
