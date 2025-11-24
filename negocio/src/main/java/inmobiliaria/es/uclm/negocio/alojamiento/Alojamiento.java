package inmobiliaria.es.uclm.negocio.alojamiento;

import java.math.BigDecimal;
import java.time.LocalDateTime; // Necesario para las fechas
import jakarta.persistence.*;
import inmobiliaria.es.uclm.negocio.user.User;

@Entity
@Table(name = "inmueble")
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

    // CAMBIO 1: Usar columnDefinition para asegurar que coincida con el SQL TEXT
    @Column(nullable = false, columnDefinition = "TEXT")
    private String direccion;

    // CAMBIO 1: Lo mismo aquí
    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    private int capacidad;

    @Column(name = "precio_noche", nullable = false)
    private BigDecimal precio;

    @Column(name = "url_imagen_principal")
    private String fotoUrl;

    @Column(name = "distancia_centro")
    private BigDecimal distanciaCentro;

    // CAMBIO 2: Añadir campos que existen en SQL (Obligatorio para validate)
    @Column(name = "is_active")
    private Boolean isActive = true;

    // Usamos String para simplificar, o crea el Enum si quieres ser estricto
   @Column(name = "politica_cancelacion", columnDefinition = "ENUM('NO_REEMBOLSABLE', 'FLEXIBLE', 'ESTRICTA') DEFAULT 'ESTRICTA'")
    private String politicaCancelacion;
    // CAMBIO 3: Eliminar valoracionMedia de la BD
    // Como NO existe columna en la tabla 'inmueble', usamos @Transient
    // para que Hibernate lo ignore al validar contra la BD.
   @Transient 
    private Double valoracionMedia;

    // CAMBIO 4: Las fechas IGUAL que en User (Espejo exacto del SQL)
    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", columnDefinition = "DATETIME")
    private LocalDateTime updatedAt;

    // --- Timestamps ---
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // --- GETTERS Y SETTERS ---
    // (Incluye los nuevos getters/setters para isActive, fechas, etc.)

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

    // Getter para valoracionMedia (aunque no se guarde en BD)
    public Double getValoracionMedia() { return valoracionMedia; }
    public void setValoracionMedia(Double valoracionMedia) { this.valoracionMedia = valoracionMedia; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}