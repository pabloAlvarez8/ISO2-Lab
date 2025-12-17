package inmobiliaria.es.uclm.negocio.alojamiento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import inmobiliaria.es.uclm.negocio.reserva.Reserva;
import inmobiliaria.es.uclm.negocio.user.User;
import lombok.Data;

import java.util.ArrayList; 
import java.util.List;

@Data
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

    @Column(nullable = false, length = 1000)
    private String direccion;

    @Column(length = 2000)
    private String descripcion;

    @Column(nullable = false)
    private int capacidad;

    
    @Column(name = "precio", nullable = false)
    private BigDecimal precio; 

    // CAMBIO 2: Renombrado a 'urlImagenPrincipal' para coincidir con el HTML
    @Column(name = "fotoUrl")
    private String fotoUrl;

    @Column(name = "distancia_centro")
    private BigDecimal distanciaCentro;

    // Inicializamos a true por defecto
    @Column(name = "is_active")
    private Boolean active = true; 

    @Column(name = "politica_cancelacion")
    private String politicaCancelacion;

    @Transient
    private Double valoracionMedia;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Esto le dice a Java: Si borras el Alojamiento, borra también sus Reservas.
    @OneToMany(mappedBy = "alojamiento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reserva> reservas = new ArrayList<>();

    // --- AUTOMATIZACIÓN DE FECHAS ---
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (updatedAt == null) updatedAt = LocalDateTime.now();
        if (politicaCancelacion == null) politicaCancelacion = "ESTRICTA";
        if (active == null) active = true;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // GETTERS Y SETTERS 
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

    // Getter/Setter ajustados a precio
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    // Getter/Setter ajustados a urlImagenPrincipal
    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }

    public BigDecimal getDistanciaCentro() { return distanciaCentro; }
    public void setDistanciaCentro(BigDecimal distanciaCentro) { this.distanciaCentro = distanciaCentro; }

    // CAMBIO 3: El getter/setter estándar de boolean es isActive / setActive
    public Boolean isActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public String getPoliticaCancelacion() { return politicaCancelacion; }
    public void setPoliticaCancelacion(String politicaCancelacion) { this.politicaCancelacion = politicaCancelacion; }

    public Double getValoracionMedia() { return valoracionMedia; }
    public void setValoracionMedia(Double valoracionMedia) { this.valoracionMedia = valoracionMedia; }

    // Solo Getters para las fechas (se gestionan solas)
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    
    // Setters necesarios por si Hibernate los pide
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<Reserva> getReservas() { return reservas; }
    public void setReservas(List<Reserva> reservas) { this.reservas = reservas; }


}