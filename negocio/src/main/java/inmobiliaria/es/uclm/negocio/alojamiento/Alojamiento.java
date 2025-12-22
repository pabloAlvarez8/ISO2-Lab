package inmobiliaria.es.uclm.negocio.alojamiento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import inmobiliaria.es.uclm.negocio.reserva.Reserva;
import inmobiliaria.es.uclm.negocio.user.User;
import lombok.Data;

import inmobiliaria.es.uclm.negocio.valoracion.ValoracionInmueble;

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

    @OneToMany(mappedBy = "inmueble", fetch = FetchType.EAGER)
    private List<ValoracionInmueble> valoraciones = new ArrayList<>();

    @Transient
    private Double valoracionMedia;

    // MODIFICAR ESTE GETTER para que calcule la media real
    public Double getValoracionMedia() {
        if (this.valoraciones == null || this.valoraciones.isEmpty()) {
            return 0.0; // Si no hay opiniones, un 0 o "Nuevo"
        }
        // Cálculo de la media usando Streams de Java
        return this.valoraciones.stream()
                .mapToDouble(ValoracionInmueble::getPuntuacion)
                .average()
                .orElse(0.0);
    }

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


}