package inmobiliaria.es.uclm.negocio.valoracion;

import inmobiliaria.es.uclm.negocio.alojamiento.Alojamiento;
import inmobiliaria.es.uclm.negocio.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal; // Importante para el precio
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ValoracionRepositoryTest {

    @Autowired
    private ValoracionRepository valoracionRepo;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void obtenerMediaPuntuacion_DeberiaCalcularElPromedioCorrecto() {
        // --- GIVEN ---

        // 1. Crear Usuario (Anfitrión/Autor)
        User user = new User();
        user.setEmail("test@test.com");
        user.setNombre("Test User");
        user.setApellido("Test Apellido");
        user.setPassword("1234"); // Campos mínimos
        entityManager.persist(user);

        // 2. Crear Alojamiento (Rellenando campos obligatorios de tu clase Alojamiento)
        Alojamiento aloj = new Alojamiento();
        aloj.setAnfitrion(user); // Obligatorio
        aloj.setNombre("Casa Test"); // Obligatorio
        aloj.setCiudad("Toledo"); // Obligatorio
        aloj.setDireccion("Calle Ancha 1"); // Obligatorio
        aloj.setTipo("Apartamento"); // Obligatorio
        aloj.setCapacidad(4); // Obligatorio
        aloj.setPrecio(new BigDecimal("100.50")); // CORREGIDO: BigDecimal
        
        entityManager.persist(aloj);

        // 3. Crear Valoraciones
        ValoracionInmueble v1 = new ValoracionInmueble(aloj, user, 4.0, "Bien");
        ValoracionInmueble v2 = new ValoracionInmueble(aloj, user, 5.0, "Excelente");
        
        entityManager.persist(v1);
        entityManager.persist(v2);
        
        entityManager.flush(); // Forzar guardado en BD antes de consultar

        // --- WHEN ---
        Double media = valoracionRepo.obtenerMediaPuntuacion(aloj.getId());

        // --- THEN ---
        assertNotNull(media);
        assertEquals(4.5, media, "La media de 4.0 y 5.0 debería ser 4.5");
    }

    @Test
    void findByInmuebleIdOrderByCreatedAtDesc_DeberiaOrdenarPorFecha() {
        // --- GIVEN ---
        
        // Setup básico
        User user = new User();
        user.setEmail("u@u.com");
        user.setPassword("x");
        user.setNombre("U");
        user.setApellido("Test Apellido");
        entityManager.persist(user);

        Alojamiento aloj = new Alojamiento();
        aloj.setAnfitrion(user);
        aloj.setNombre("Piso");
        aloj.setCiudad("Madrid");
        aloj.setDireccion("Gran Via");
        aloj.setTipo("Piso");
        aloj.setCapacidad(2);
        aloj.setPrecio(new BigDecimal("50.00")); // CORREGIDO
        entityManager.persist(aloj);

        // Creamos v1
        ValoracionInmueble v1 = new ValoracionInmueble(aloj, user, 1.0, "Comentario Viejo");
        entityManager.persist(v1);
        entityManager.flush(); // Hacemos flush para asegurar que se guarda con un timestamp

        // Pequeña pausa simulada o simplemente confiar en el orden de inserción de Hibernate
        // Creamos v2
        ValoracionInmueble v2 = new ValoracionInmueble(aloj, user, 1.0, "Comentario Nuevo");
        entityManager.persist(v2);
        entityManager.flush();

        // --- WHEN ---
        List<ValoracionInmueble> lista = valoracionRepo.findByInmuebleIdOrderByCreatedAtDesc(aloj.getId());

        // --- THEN ---
        assertEquals(2, lista.size());
        // El más reciente (v2 - Nuevo) debe ir primero (índice 0)
        assertEquals("Comentario Nuevo", lista.get(0).getComentario());
        assertEquals("Comentario Viejo", lista.get(1).getComentario());
    }
}