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

    // SOLUCIÓN 1: Longitud exacta del VARCHAR para coincidir con SQL
    @Column(name = "correo", nullable = false, unique = true, length = 191) 
    private String email;

    @Column(name = "contrasena", nullable = false)
    private String password;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    private String direccion;

    @Column(name = "url_foto_perfil")
    private String urlFotoPerfil;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.INQUILINO;

    // SOLUCIÓN 2: Forzar el tipo TIMESTAMP para created_at
    // Hibernate por defecto usa DATETIME, pero en tu BD es TIMESTAMP. Esto arregla el choque.
    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    // SOLUCIÓN 3: Forzar el tipo DATETIME para updated_at
    // Especificamos explícitamente DATETIME para que no haya dudas.
    @Column(name = "updated_at", columnDefinition = "DATETIME")
    private LocalDateTime updatedAt;

    // --- Timestamps (Marcas de tiempo) ---
    @PrePersist
    protected void onCreate() {
        // Asignamos la fecha en Java también para asegurar consistencia
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

    // toString (Opcional, pero útil para depurar)
    @Override
    public String toString() {
        return "User{id=" + id + ", email='" + email + "', role=" + role + "}";
    }
}