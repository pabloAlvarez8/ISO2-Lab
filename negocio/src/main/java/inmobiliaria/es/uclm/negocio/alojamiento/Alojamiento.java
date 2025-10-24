package inmobiliaria.es.uclm.negocio.alojamiento;

import java.math.BigDecimal;
import jakarta.persistence.*;
import inmobiliaria.es.uclm.negocio.user.User; // 1. Importa la entidad User

@Entity
@Table(name = "inmueble") // 2. Apunta a la tabla correcta
public class Alojamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 3. Añade la relación OBLIGATORIA con el anfitrión
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_anfitrion", nullable = false)
    private User anfitrion;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private String nombre;

    @Lob // La columna 'direccion' es TEXT, @Lob es mejor para textos largos
    @Column(nullable = false)
    private String direccion;

    @Column(nullable = false)
    private String ciudad;

    @Lob // La columna 'descripcion' es TEXT
    private String descripcion;

    @Column(nullable = false)
    private int capacidad;

    // 4. Mapea el campo 'precio' a la columna 'precio_noche'
    @Column(name = "precio_noche", nullable = false)
    private BigDecimal precio;

    // 5. Mapea 'fotoUrl' a la columna 'url_imagen_principal'
    @Column(name = "url_imagen_principal")
    private String fotoUrl;

    @Column(name = "valoracion_media")
    private double valoracionMedia;

    @Column(name = "distancia_centro")
    private BigDecimal distanciaCentro;

    // ... (También faltarían is_active, politica_cancelacion, etc.,
    // pero estos son los mínimos para que funcione)

    // Getters y setters (¡actualizados con los nuevos campos!)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getAnfitrion() {
        return anfitrion;
    }

    public void setAnfitrion(User anfitrion) {
        this.anfitrion = anfitrion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public BigDecimal getDistanciaCentro() {
        return distanciaCentro;
    }

    public void setDistanciaCentro(BigDecimal distanciaCentro) {
        this.distanciaCentro = distanciaCentro;
    }

    public double getValoracionMedia() {
        return valoracionMedia;
    }

    public void setValoracionMedia(Double valoracionMedia) {
        this.valoracionMedia = valoracionMedia;
    }
    // O 'double' si prefieres para ratings

    // ... y sus getters/setters
}