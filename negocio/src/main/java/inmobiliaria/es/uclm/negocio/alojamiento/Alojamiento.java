package inmobiliaria.es.uclm.negocio.alojamiento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import inmobiliaria.es.uclm.negocio.user.User;
import lombok.Data;

@Data
@Entity
@Table(name = "inmueble") // Asegúrate de que en data.sql insertas en 'inmueble' (singular)
public class Alojamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_anfitrion", nullable = false)
    private User anfitrion;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String ciudad;

    // CORRECCIÓN 1: Derby no soporta TEXT.
    // Al quitar columnDefinition, Hibernate usará VARCHAR(255).
    // Si necesitas más espacio, usa length = 1000 (o hasta 32000).
    @Column(nullable = false, length = 1000)
    private String direccion;

    @Column(length = 2000) // Le damos más espacio para la descripción
    private String descripcion;

    @Column(nullable = false)
    private int capacidad;

    @Column(name = "precio_noche", nullable = false)
    private BigDecimal precio;

    @Column(name = "url_imagen_principal")
    private String fotoUrl;

    @Column(name = "distancia_centro")
    private BigDecimal distanciaCentro;

    @Column(name = "is_active")
    private Boolean isActive = true;

    // CORRECCIÓN 2: Derby no soporta ENUM en SQL nativo fácilmente.
    // Lo guardamos como String simple. Hibernate lo gestiona bien.
    @Column(name = "politica_cancelacion")
    private String politicaCancelacion;

    @Transient
    private Double valoracionMedia;

    // CORRECCIÓN 3: Derby O-D-I-A 'DATETIME'. Solo entiende 'TIMESTAMP'.
    // Quitamos los columnDefinition y dejamos que Hibernate elija el tipo correcto (TIMESTAMP).
    // Además, quitamos el DEFAULT SQL porque ya lo haces en el @PrePersist de Java.
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // --- Timestamps (Esto es lo que realmente guarda la fecha, no el SQL) ---
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
        // Valor por defecto para política si es nula
        if (politicaCancelacion == null) politicaCancelacion = "ESTRICTA";
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}