package inmobiliaria.es.uclm.negocio.alojamiento;

import java.math.BigDecimal;
import jakarta.persistence.*;
import inmobiliaria.es.uclm.negocio.user.User;

/**
 * Entidad JPA que representa un Alojamiento.
 * Se mapea contra la tabla 'inmueble'.
 */
@Entity
@Table(name = "inmueble")
public class Alojamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Relación obligatoria con el anfitrión (User).
    // LAZY fetch para optimizar la carga.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_anfitrion", nullable = false)
    private User anfitrion;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private String nombre;

    // @Lob se usa para mapear a tipos TEXT/CLOB de SQL,
    // adecuados para Strings largos.
    @Lob
    @Column(nullable = false)
    private String direccion;

    @Column(nullable = false)
    private String ciudad;

    @Lob
    private String descripcion;

    @Column(nullable = false)
    private int capacidad;

    // Mapeo explícito del campo 'precio' a la columna 'precio_noche'.
    // Se usa BigDecimal para precisión monetaria.
    @Column(name = "precio_noche", nullable = false)
    private BigDecimal precio;

    // Mapeo explícito a la columna de imagen.
    @Column(name = "url_imagen_principal")
    private String fotoUrl;

    @Column(name = "valoracion_media")
    private Double valoracionMedia;

    @Column(name = "distancia_centro")
    private BigDecimal distanciaCentro;

    // --- Getters y Setters ---
    // Requeridos por JPA.

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public Double getValoracionMedia() {
        return valoracionMedia;
    }

    public void setValoracionMedia(Double valoracionMedia) {
        this.valoracionMedia = valoracionMedia;
    }
}