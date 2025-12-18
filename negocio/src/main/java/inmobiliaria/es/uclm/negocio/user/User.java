package inmobiliaria.es.uclm.negocio.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario") // "usuario" evita conflictos con la palabra reservada "USER" de Derby
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

    @Column(name = "cuenta_bancaria")
    private String cuentaBancaria;

    // --- TIMESTAMPS ---
    // Hibernate mapeará esto automáticamente a TIMESTAMP en Derby

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Estos métodos se ejecutan automáticamente antes de guardar en la BD
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        // Inicializamos ambos al crear
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