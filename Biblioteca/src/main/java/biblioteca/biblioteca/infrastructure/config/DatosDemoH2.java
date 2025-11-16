package biblioteca.biblioteca.infrastructure.config;

import biblioteca.biblioteca.infrastructure.persistence.jpa.entity.RolEntity;
import biblioteca.biblioteca.infrastructure.persistence.jpa.entity.UsuarioEntity;
import biblioteca.biblioteca.infrastructure.persistence.jpa.spring.RolSpringDataRepository;
import biblioteca.biblioteca.infrastructure.persistence.jpa.spring.UsuarioSpringDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import biblioteca.biblioteca.domain.model.Categoria;
import biblioteca.biblioteca.infrastructure.persistence.jpa.entity.AutorEntity;
import biblioteca.biblioteca.infrastructure.persistence.jpa.entity.EditorialEntity;
import biblioteca.biblioteca.infrastructure.persistence.jpa.entity.LibroEntity;
import biblioteca.biblioteca.infrastructure.persistence.jpa.spring.AutorSpringDataRepository;
import biblioteca.biblioteca.infrastructure.persistence.jpa.spring.EditorialSpringDataRepository;
import biblioteca.biblioteca.infrastructure.persistence.jpa.spring.LibroSpringDataRepository;

import java.time.LocalDate;
import java.util.List;


import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class DatosDemoH2 implements CommandLineRunner {

    private final RolSpringDataRepository rolRepo;
    private final UsuarioSpringDataRepository usuarioRepo;
    private final PasswordEncoder passwordEncoder;

    private final AutorSpringDataRepository autorRepo;
    private final EditorialSpringDataRepository editorialRepo;
    private final LibroSpringDataRepository libroRepo;

    @Override
    public void run(String... args) {
        // 1) Asegurar roles (solo si faltan)
        RolEntity rolLector        = ensureRol("LECTOR");
        RolEntity rolBibliotecario = ensureRol("BIBLIOTECARIO");
        RolEntity rolAdmin         = ensureRol("ADMINISTRADOR");

        // 2) Crear usuarios demo si no existen por email
        createUserIfMissing(
                "lector@demo.com", "Lector Demo", "20000001",
                passwordEncoder.encode("password"),
                Set.of(rolLector),
                1 // lectorId opcional de ejemplo
        );

        createUserIfMissing(
                "bibliotecario@demo.com", "Bibliotecario Demo", "20000002",
                passwordEncoder.encode("password"),
                Set.of(rolBibliotecario),
                null
        );

        createUserIfMissing(
                "admin@demo.com", "Admin Demo", "20000003",
                passwordEncoder.encode("password"),
                Set.of(rolAdmin),
                null
        );

        createUserIfMissing(
                "multi@demo.com", "Multi Rol Demo", "20000004",
                passwordEncoder.encode("password"),
                Set.of(rolLector, rolBibliotecario),
                2 // otro lectorId de ejemplo (o null si preferís)
        );

        cargarLibrosDemo();
    }

    private RolEntity ensureRol(String nombre) {
        Optional<RolEntity> found = rolRepo.findByNombre(nombre);
        return found.orElseGet(() -> rolRepo.save(new RolEntity(null, nombre)));
    }

    private void createUserIfMissing(String email, String nombreCompleto, String dni,
                                     String passwordHash, Set<RolEntity> roles, Integer lectorId) {
        usuarioRepo.findByEmail(email.toLowerCase()).ifPresentOrElse(
                u -> { /* ya existe, no hacemos nada */ },
                () -> {
                    UsuarioEntity e = new UsuarioEntity();
                    e.setId(null);
                    e.setNombreCompleto(nombreCompleto);
                    e.setDni(dni);
                    e.setEmail(email.toLowerCase());
                    e.setPasswordHash(passwordHash);
                    e.setLectorId(lectorId);
                    e.setRoles(new HashSet<>(roles));
                    usuarioRepo.save(e);
                }
        );
    }

    private void cargarLibrosDemo() {
        // Si ya hay libros, no hago nada (por si cambias H2 por otra BD)
        if (!libroRepo.findAll().isEmpty()) {
            return;
        }

        // -------- Editoriales --------
        EditorialEntity planeta = editorialRepo.save(
                new EditorialEntity(null, "Planeta")
        );
        EditorialEntity sudamericana = editorialRepo.save(
                new EditorialEntity(null, "Sudamericana")
        );
        EditorialEntity anagrama = editorialRepo.save(
                new EditorialEntity(null, "Anagrama")
        );

        // -------- Autores --------
        AutorEntity borges = autorRepo.save(
                new AutorEntity(null, "Jorge Luis Borges",
                        LocalDate.of(1899, 8, 24), "Argentina")
        );
        AutorEntity cortazar = autorRepo.save(
                new AutorEntity(null, "Julio Cortázar",
                        LocalDate.of(1914, 8, 26), "Argentina")
        );
        AutorEntity garciaMarquez = autorRepo.save(
                new AutorEntity(null, "Gabriel García Márquez",
                        LocalDate.of(1927, 3, 6), "Colombia")
        );

        // -------- Libros --------
        LibroEntity l1 = new LibroEntity(
                null,
                "El Aleph",
                1949,
                borges.getId(),
                sudamericana.getId(),
                Categoria.Novela,
                "Colección de cuentos fantásticos y metafísicos de Borges."
        );

        LibroEntity l2 = new LibroEntity(
                null,
                "Rayuela",
                1963,
                cortazar.getId(),
                anagrama.getId(),
                Categoria.Novela,
                "Novela del boom latinoamericano con estructura no lineal."
        );

        LibroEntity l3 = new LibroEntity(
                null,
                "Cien años de soledad",
                1967,
                garciaMarquez.getId(),
                planeta.getId(),
                Categoria.Novela,
                "Saga de la familia Buendía en Macondo, clásico del realismo mágico."
        );

        libroRepo.saveAll(List.of(l1, l2, l3));
    }

}
