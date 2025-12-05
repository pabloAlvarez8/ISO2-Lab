package inmobiliaria.es.uclm.negocio.alojamiento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import inmobiliaria.es.uclm.negocio.user.User;

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

    // --- GETTERS Y SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getAnfitrion() { return anfitrion; }
    public void setAnfitrion(User anfitrion) { this.anfitrion = anfitrion; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }

    public BigDecimal getDistanciaCentro() { return distanciaCentro; }
    public void setDistanciaCentro(BigDecimal distanciaCentro) { this.distanciaCentro = distanciaCentro; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public String getPoliticaCancelacion() { return politicaCancelacion; }
    public void setPoliticaCancelacion(String politicaCancelacion) { this.politicaCancelacion = politicaCancelacion; }

    public Double getValoracionMedia() { return valoracionMedia; }
    public void setValoracionMedia(Double valoracionMedia) { this.valoracionMedia = valoracionMedia; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}