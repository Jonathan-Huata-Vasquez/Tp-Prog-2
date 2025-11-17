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
import biblioteca.biblioteca.domain.model.EstadoCopia;
import biblioteca.biblioteca.infrastructure.persistence.jpa.entity.AutorEntity;
import biblioteca.biblioteca.infrastructure.persistence.jpa.entity.EditorialEntity;
import biblioteca.biblioteca.infrastructure.persistence.jpa.entity.LibroEntity;
import biblioteca.biblioteca.infrastructure.persistence.jpa.entity.CopiaEntity;
import biblioteca.biblioteca.infrastructure.persistence.jpa.entity.LectorEntity;
import biblioteca.biblioteca.infrastructure.persistence.jpa.entity.PrestamoEntity;
import biblioteca.biblioteca.infrastructure.persistence.jpa.spring.AutorSpringDataRepository;
import biblioteca.biblioteca.infrastructure.persistence.jpa.spring.EditorialSpringDataRepository;
import biblioteca.biblioteca.infrastructure.persistence.jpa.spring.LibroSpringDataRepository;
import biblioteca.biblioteca.infrastructure.persistence.jpa.spring.CopiaSpringDataRepository;
import biblioteca.biblioteca.infrastructure.persistence.jpa.spring.LectorSpringDataRepository;
import biblioteca.biblioteca.infrastructure.persistence.jpa.spring.PrestamoSpringDataRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class DatosDemoH2 implements CommandLineRunner {

    private static final String DEMO_PASSWORD = "password";

    private final RolSpringDataRepository rolRepo;
    private final UsuarioSpringDataRepository usuarioRepo;
    private final PasswordEncoder passwordEncoder;

    private final AutorSpringDataRepository autorRepo;
    private final EditorialSpringDataRepository editorialRepo;
    private final LibroSpringDataRepository libroRepo;
    private final CopiaSpringDataRepository copiaRepo;
    private final LectorSpringDataRepository lectorRepo;
    private final PrestamoSpringDataRepository prestamoRepo;

    @Override
    public void run(String... args) {
        // 1) Asegurar roles (solo si faltan)
        RolEntity rolLector        = ensureRol("LECTOR");
        RolEntity rolBibliotecario = ensureRol("BIBLIOTECARIO");
        RolEntity rolAdmin         = ensureRol("ADMINISTRADOR");

        // 2) Crear usuarios demo si no existen por email
        createUserIfMissing(
                "lector@demo.com", "Lector Demo", "20000001",
                passwordEncoder.encode(DEMO_PASSWORD),
                Set.of(rolLector),
                1 // lectorId opcional de ejemplo
        );

        createUserIfMissing(
                "bibliotecario@demo.com", "Bibliotecario Demo", "20000002",
                passwordEncoder.encode(DEMO_PASSWORD),
                Set.of(rolBibliotecario),
                null
        );

        createUserIfMissing(
                "admin@demo.com", "Admin Demo", "20000003",
                passwordEncoder.encode(DEMO_PASSWORD),
                Set.of(rolAdmin),
                null
        );

        createUserIfMissing(
                "multi@demo.com", "Multi Rol Demo", "20000004",
                passwordEncoder.encode(DEMO_PASSWORD),
                Set.of(rolLector, rolBibliotecario),
                2 // otro lectorId de ejemplo (o null si preferís)
        );

        createUserIfMissing(
                "ana@demo.com", "Ana Martínez", "20000005",
                passwordEncoder.encode(DEMO_PASSWORD),
                Set.of(rolLector),
                4 // lectorId que corresponde al lector bloqueado
        );

        cargarLibrosDemo();
        cargarCopiasYPrestamosDemo();
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

    private void cargarCopiasYPrestamosDemo() {
        // Si ya hay copias, no hacer nada
        if (!copiaRepo.findAll().isEmpty()) {
            return;
        }

        // -------- Lectores --------
        LectorEntity lector1 = lectorRepo.save(
                new LectorEntity(null, "Juan Pérez", null)
        );
        LectorEntity lector2 = lectorRepo.save(
                new LectorEntity(null, "María González", null)
        );
        LectorEntity lector3 = lectorRepo.save(
                new LectorEntity(null, "Carlos Rodríguez", null)
        );
        
        // Lector bloqueado por préstamo reciente con atraso
        LocalDate hoy = LocalDate.now();
        LocalDate bloqueadoHasta = hoy.plusDays(8); // Bloqueado por 8 días más
        LectorEntity lectorBloqueado = lectorRepo.save(
                new LectorEntity(null, "Ana Martínez", bloqueadoHasta)
        );

        // Obtener los libros ya creados
        List<LibroEntity> libros = libroRepo.findAll();
        LibroEntity aleph = libros.stream().filter(l -> "El Aleph".equals(l.getTitulo())).findFirst().orElse(null);
        LibroEntity rayuela = libros.stream().filter(l -> "Rayuela".equals(l.getTitulo())).findFirst().orElse(null);
        LibroEntity cienAnios = libros.stream().filter(l -> "Cien años de soledad".equals(l.getTitulo())).findFirst().orElse(null);

        if (aleph == null || rayuela == null || cienAnios == null) {
            return; // Si no hay libros, no crear copias
        }

        // -------- Copias del Aleph --------
        copiaRepo.save(new CopiaEntity(null, aleph.getId(), EstadoCopia.EnBiblioteca)); // Disponible
        CopiaEntity alephCopia2 = copiaRepo.save(
                new CopiaEntity(null, aleph.getId(), EstadoCopia.Prestada)     // Prestada - usada en préstamo
        );
        copiaRepo.save(new CopiaEntity(null, aleph.getId(), EstadoCopia.EnBiblioteca)); // Disponible

        // -------- Copias de Rayuela --------
        CopiaEntity rayuelaCopia1 = copiaRepo.save(
                new CopiaEntity(null, rayuela.getId(), EstadoCopia.Prestada)    // Prestada - usada en préstamo
        );
        CopiaEntity rayuelaCopia2 = copiaRepo.save(
                new CopiaEntity(null, rayuela.getId(), EstadoCopia.EnBiblioteca) // Disponible - usada para préstamo histórico
        );
        copiaRepo.save(new CopiaEntity(null, rayuela.getId(), EstadoCopia.EnReparacion)); // En reparación
        copiaRepo.save(new CopiaEntity(null, rayuela.getId(), EstadoCopia.EnBiblioteca)); // Disponible

        // -------- Copias de Cien años de soledad --------
        copiaRepo.save(new CopiaEntity(null, cienAnios.getId(), EstadoCopia.EnBiblioteca)); // Disponible
        copiaRepo.save(new CopiaEntity(null, cienAnios.getId(), EstadoCopia.EnBiblioteca)); // Disponible
        copiaRepo.save(new CopiaEntity(null, cienAnios.getId(), EstadoCopia.ConRetraso));       // Perdida
        CopiaEntity cienAniosCopia4 = copiaRepo.save(
                new CopiaEntity(null, cienAnios.getId(), EstadoCopia.Prestada)      // Prestada - usada en préstamo
        );
        copiaRepo.save(new CopiaEntity(null, cienAnios.getId(), EstadoCopia.EnBiblioteca)); // Disponible

        // -------- Préstamos activos --------
        
        // Préstamo 1: Juan tiene una copia de El Aleph (prestada hace 5 días, vence en 16 días)
        prestamoRepo.save(new PrestamoEntity(
                null,
                lector1.getId(),
                alephCopia2.getId(),
                hoy.minusDays(5),
                hoy.plusDays(16),
                null // no devuelto
        ));

        // Préstamo 2: María tiene una copia de Rayuela (prestada hace 10 días, vence en 11 días)
        prestamoRepo.save(new PrestamoEntity(
                null,
                lector2.getId(),
                rayuelaCopia1.getId(),
                hoy.minusDays(10),
                hoy.plusDays(11),
                null // no devuelto
        ));

        // Préstamo 3: Carlos tiene una copia de Cien años de soledad (prestada hace 2 días, vence en 19 días)
        prestamoRepo.save(new PrestamoEntity(
                null,
                lector3.getId(),
                cienAniosCopia4.getId(),
                hoy.minusDays(2),
                hoy.plusDays(19),
                null // no devuelto
        ));

        // Préstamo 4: Préstamo ya devuelto (María devolvió una copia hace 5 días)
        // Usamos una copia existente que ahora está disponible
        prestamoRepo.save(new PrestamoEntity(
                null,
                lector2.getId(),
                rayuelaCopia2.getId(), // Usa la copia que ahora está EnBiblioteca
                hoy.minusDays(35),
                hoy.minusDays(14),
                hoy.minusDays(5) // devuelto hace 5 días
        ));

        // -------- Préstamos para lector bloqueado (Ana Martínez) --------
        // Crear copia adicional para el lector bloqueado
        CopiaEntity rayuelaCopiaAna = copiaRepo.save(
                new CopiaEntity(null, rayuela.getId(), EstadoCopia.EnBiblioteca) // Ahora disponible
        );

        // Préstamo devuelto CON ATRASO reciente que causó el bloqueo
        // Prestado hace 30 días, vencía hace 9 días, devuelto hace 3 días (6 días de atraso)
        // Regla: 2 días de bloqueo por cada día de atraso = 6 * 2 = 12 días de bloqueo
        // Si fue devuelto hace 3 días y el bloqueo es por 12 días, quedan 9 días
        LocalDate fechaPrestamoReciente = hoy.minusDays(30);
        LocalDate fechaVencimientoReciente = fechaPrestamoReciente.plusDays(21); // hace 9 días
        LocalDate fechaDevolucionTarde = hoy.minusDays(3); // devuelto hace 3 días
        
        prestamoRepo.save(new PrestamoEntity(
                null,
                lectorBloqueado.getId(),
                rayuelaCopiaAna.getId(),
                fechaPrestamoReciente,    // prestado hace 30 días
                fechaVencimientoReciente, // vencía hace 9 días  
                fechaDevolucionTarde      // devuelto hace 3 días (6 días tarde)
        ));

        // Agregar otro préstamo anterior devuelto a tiempo para mostrar historial
        CopiaEntity alephCopiaAnaAnterior = copiaRepo.save(
                new CopiaEntity(null, aleph.getId(), EstadoCopia.EnBiblioteca)
        );
        
        prestamoRepo.save(new PrestamoEntity(
                null,
                lectorBloqueado.getId(),
                alephCopiaAnaAnterior.getId(),
                hoy.minusDays(60),        // prestado hace 60 días
                hoy.minusDays(39),        // vencía hace 39 días
                hoy.minusDays(42)         // devuelto hace 42 días (a tiempo)
        ));

        // -------- Préstamos para lector demo (lectorId = 1) --------
        // Necesitamos crear copias adicionales para estos préstamos
        CopiaEntity alephCopiaDemo = copiaRepo.save(
                new CopiaEntity(null, aleph.getId(), EstadoCopia.ConRetraso) // Con retraso
        );
        CopiaEntity rayuelaCopiaDemo = copiaRepo.save(
                new CopiaEntity(null, rayuela.getId(), EstadoCopia.EnBiblioteca) // Para préstamo devuelto
        );

        // Préstamo 1 para lector demo: Préstamo con retraso (vencido hace 3 días)
        prestamoRepo.save(new PrestamoEntity(
                null,
                1, // lectorId del usuario lector@demo.com
                alephCopiaDemo.getId(),
                hoy.minusDays(25), // prestado hace 25 días
                hoy.minusDays(3),  // vencía hace 3 días
                null // no devuelto - CON RETRASO
        ));

        // Préstamo 2 para lector demo: Préstamo devuelto a tiempo
        prestamoRepo.save(new PrestamoEntity(
                null,
                1, // lectorId del usuario lector@demo.com
                rayuelaCopiaDemo.getId(),
                hoy.minusDays(20), // prestado hace 20 días
                hoy.minusDays(5),  // vencía hace 5 días
                hoy.minusDays(7)   // devuelto hace 7 días - DEVUELTO A TIEMPO
        ));

        // Préstamo 3 para lector demo: Préstamo DEVUELTO CON ATRASO (hace un año)
        // Esto debería activar las reglas de bloqueo del dominio Lector
        CopiaEntity cienAniosCopiaDemo = copiaRepo.save(
                new CopiaEntity(null, cienAnios.getId(), EstadoCopia.EnBiblioteca) // Ahora disponible nuevamente
        );
        
        LocalDate fechaPrestamoAntiguo = hoy.minusYears(1).minusDays(30); // Prestado hace 1 año y 30 días
        LocalDate fechaVencimientoAntiguo = fechaPrestamoAntiguo.plusDays(21); // Vencía 21 días después (hace 1 año y 9 días)
        fechaDevolucionTarde = fechaVencimientoAntiguo.plusDays(15); // Devuelto 15 días tarde
        
        prestamoRepo.save(new PrestamoEntity(
                null,
                1, // lectorId del usuario lector@demo.com
                cienAniosCopiaDemo.getId(),
                fechaPrestamoAntiguo,     // hace 1 año y 30 días
                fechaVencimientoAntiguo,  // vencía hace 1 año y 9 días
                fechaDevolucionTarde      // devuelto hace ~11 meses (15 días de atraso)
        ));
    }

}
