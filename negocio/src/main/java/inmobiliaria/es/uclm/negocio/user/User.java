package inmobiliaria.es.uclm.negocio.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * Entidad JPA que representa a un usuario en el sistema.
 * <p>
 * Esta clase mapea la tabla "usuario" de la base de datos y define la estructura
 * de datos fundamental para la autenticación y gestión de perfiles.
 * Utiliza anotaciones de Lombok para reducir el código repetitivo (getters/setters).
 */
@Entity
@Table(name = "usuario")
@Getter
@Setter
public class User {

    /**
     * Identificador único del usuario.
     * Generado automáticamente por la base de datos (estrategia IDENTITY).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Correo electrónico del usuario, utilizado como nombre de usuario para el login.
     * Mapeado a la columna "correo" con restricción de unicidad.
     */
    @Column(name = "correo", nullable = false, unique = true)
    private String email;

    /**
     * Contraseña del usuario cifrada.
     * Mapeada a la columna "contrasena". Nunca debe almacenarse en texto plano.
     */
    @Column(name = "contrasena", nullable = false)
    private String password;

    /**
     * Nombre de pila del usuario.
     */
    @Column(nullable = false)
    private String nombre;

    /**
     * Apellidos del usuario.
     */
    @Column(nullable = false)
    private String apellido;

    /**
     * Dirección física del usuario (opcional).
     */
    private String direccion;

    /**
     * URL o ruta relativa a la imagen de perfil del usuario.
     */
    @Column(name = "url_foto_perfil")
    private String urlFotoPerfil;

    /**
     * Rol asignado al usuario para la gestión de permisos.
     * Se almacena como cadena de texto (STRING) en la base de datos.
     * Valor por defecto: {@link Role#INQUILINO}.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.INQUILINO;

    /**
     * Marca de tiempo de la creación del registro.
     * No es actualizable una vez insertado.
     */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Marca de tiempo de la última actualización del registro.
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Método ejecutado automáticamente antes de persistir la entidad por primera vez.
     * Inicializa las fechas de creación y actualización.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * Método ejecutado automáticamente antes de actualizar la entidad en base de datos.
     * Refresca la fecha de actualización.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Enumeración que define los roles disponibles en la aplicación.
     */
    public enum Role {
        INQUILINO, PROPIETARIO
    }

    /**
     * Representación en cadena del objeto Usuario.
     * <p>
     * Nota: Por razones de seguridad, el campo contraseña se oculta en la salida
     * para evitar filtraciones en los logs del sistema.
     *
     * @return Una cadena con los datos representativos del usuario.
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", email='" + email + '\'' +
                ", password='[PROTEGIDO]'" + // Seguridad: ocultar hash
                ", direccion='" + direccion + '\'' +
                ", role=" + role +
                ", urlFotoPerfil='" + urlFotoPerfil + '\'' +
                '}';
    }
}