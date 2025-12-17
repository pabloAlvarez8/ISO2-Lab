package inmobiliaria.es.uclm.negocio.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "correo", nullable = false, unique = true, length = 191) 
    private String email;

    @Column(name = "contrasena", nullable = false)
    private String password;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(name = "telefono", unique = true)
    private String telefono;

    private String direccion;

    @Column(name = "url_foto_perfil")
    private String urlFotoPerfil;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.INQUILINO;

    @Column(name = "dni")
    private String dni;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "cuenta_bancaria")
    private String cuentaBancaria;

    // CORRECCIÓN 1: Quitamos el columnDefinition complejo.
    // Hibernate detectará que es LocalDateTime y usará TIMESTAMP automáticamente.
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // CORRECCIÓN 2: ¡CRÍTICO! Quitamos "DATETIME".
    // Derby no tiene DATETIME, usaría TIMESTAMP. Al borrar el columnDefinition,
    // dejamos que Hibernate haga la traducción correcta.
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // --- Timestamps (Esto se encarga de meter la fecha, no hace falta SQL default) ---
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum Role {
        INQUILINO, PROPIETARIO
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", email='" + email + "', role=" + role + "}";
    }
}