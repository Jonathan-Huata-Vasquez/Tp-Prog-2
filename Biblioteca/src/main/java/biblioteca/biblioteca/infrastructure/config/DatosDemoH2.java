package biblioteca.biblioteca.infrastructure.config;

import biblioteca.biblioteca.infrastructure.persistence.jpa.entity.RolEntity;
import biblioteca.biblioteca.infrastructure.persistence.jpa.entity.UsuarioEntity;
import biblioteca.biblioteca.infrastructure.persistence.jpa.spring.RolSpringDataRepository;
import biblioteca.biblioteca.infrastructure.persistence.jpa.spring.UsuarioSpringDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

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
}
