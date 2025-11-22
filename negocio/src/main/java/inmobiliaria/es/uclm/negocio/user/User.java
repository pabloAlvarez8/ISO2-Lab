package inmobiliaria.es.uclm.negocio.user;

import jakarta.persistence.*;
import lombok.Getter; // Usando Lombok para getters/setters (opcional)
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario") // ¡Que coincida con el nombre real de la tabla (la renombraste!)
@Getter // Anotación Lombok
@Setter // Anotación Lombok
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Usa 'email' para coincidir con tu código anterior, mapea a columna 'correo'
    @Column(name = "correo", nullable = false, unique = true)
    private String email;

    // Usa 'password', mapea a columna 'contrasena'
    @Column(name = "contrasena", nullable = false)
    private String password;

    // El nombre del campo Java coincide con la columna si se llaman igual
    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    private String direccion;

    @Column(name = "url_foto_perfil")
    private String urlFotoPerfil;

    // Usa un Enum para el Rol
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.INQUILINO; // Valor por defecto

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // --- Timestamps (Marcas de tiempo) ---
    @PrePersist // Antes de guardar por primera vez
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate // Antes de actualizar
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // --- Enum para el Rol ---
    public enum Role {
        INQUILINO, PROPIETARIO
    }

    // ... dentro de tu clase User.java ...

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", email='" + email + '\'' +
                // ¡NUNCA incluyas la contraseña en el log!
                ", password='[PROTEGIDO]'" +
                ", direccion='" + direccion + '\'' +
                ", role=" + role +
                ", urlFotoPerfil='" + urlFotoPerfil + '\'' +
                '}';
    }
    // --- Opcional: Constructores, toString ---
    // Puedes añadir constructores o un método toString si los necesitas
    // Lombok a menudo se encarga de esto, o puedes generarlos
}