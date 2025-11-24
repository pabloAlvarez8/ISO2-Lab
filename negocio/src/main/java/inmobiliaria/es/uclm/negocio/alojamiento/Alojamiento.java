package inmobiliaria.es.uclm.negocio.alojamiento;

import java.math.BigDecimal;
import jakarta.persistence.*;
import inmobiliaria.es.uclm.negocio.user.User;

/**
 * Representa un alojamiento o propiedad disponible para alquiler en el sistema.
 * <p>
 * Esta entidad mapea la tabla 'inmueble' y gestiona tanto los datos físicos
 * de la propiedad como la relación con el usuario propietario (anfitrión) y
 * los detalles económicos (precio).
 * </p>
 */
@Entity
@Table(name = "inmueble")
public class Alojamiento {

    /**
     * Identificador único del alojamiento. Generado automáticamente.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Usuario propietario del alojamiento.
     * <p>
     * Se configura con carga perezosa ({@code FetchType.LAZY}) para evitar recuperar
     * los datos del usuario en consultas masivas de alojamientos, optimizando el rendimiento.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_anfitrion", nullable = false)
    private User anfitrion;

    /**
     * Categoría del inmueble (ej. Apartamento, Casa, Villa).
     */
    @Column(nullable = false)
    private String tipo;

    /**
     * Nombre comercial o título del anuncio del alojamiento.
     */
    @Column(nullable = false)
    private String nombre;

    /**
     * Dirección física completa.
     * Se marca como {@code @Lob} para soportar cadenas de texto largas en la base de datos.
     */
    @Lob
    @Column(nullable = false)
    private String direccion;

    /**
     * Ciudad donde se ubica el alojamiento.
     */
    @Column(nullable = false)
    private String ciudad;

    /**
     * Detalle extendido de las características del alojamiento.
     * Tipo LOB para permitir descripciones extensas sin truncamiento.
     */
    @Lob
    private String descripcion;

    /**
     * Número máximo de huéspedes permitidos.
     */
    @Column(nullable = false)
    private int capacidad;

    /**
     * Costo por noche del alojamiento.
     * Se utiliza {@link BigDecimal} para garantizar la precisión en operaciones monetarias.
     */
    @Column(name = "precio_noche", nullable = false)
    private BigDecimal precio;

    /**
     * URL de la imagen de portada del alojamiento.
     */
    @Column(name = "url_imagen_principal")
    private String fotoUrl;

    /**
     * Puntuación media calculada en base a las reseñas de los usuarios.
     * Puede ser nulo si el alojamiento aún no tiene valoraciones.
     */
    @Column(name = "valoracion_media")
    private Double valoracionMedia;

    /**
     * Distancia en kilómetros desde el alojamiento al centro de la ciudad.
     */
    @Column(name = "distancia_centro")
    private BigDecimal distanciaCentro;

    // --- Getters y Setters ---

    /**
     * Obtiene el identificador único del alojamiento.
     * @return ID numérico.
     */
    public int getId() {
        return id;
    }

    /**
     * Establece el identificador del alojamiento.
     * @param id Nuevo ID.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtiene el usuario anfitrión asociado a este inmueble.
     * @return Entidad {@link User} propietaria.
     */
    public User getAnfitrion() {
        return anfitrion;
    }

    /**
     * Asigna un anfitrión al alojamiento.
     * @param anfitrion Usuario propietario (no debe ser nulo).
     */
    public void setAnfitrion(User anfitrion) {
        this.anfitrion = anfitrion;
    }

    /**
     * Obtiene el tipo de inmueble.
     * @return Cadena descriptiva del tipo (ej. "Apartamento").
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Define el tipo de inmueble.
     * @param tipo Tipo de alojamiento.
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Obtiene el nombre o título del anuncio.
     * @return Nombre del alojamiento.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre o título del anuncio.
     * @param nombre Nuevo nombre.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la dirección física.
     * @return Dirección completa.
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * Actualiza la dirección del inmueble.
     * @param direccion Nueva dirección.
     */
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    /**
     * Obtiene la ciudad de ubicación.
     * @return Nombre de la ciudad.
     */
    public String getCiudad() {
        return ciudad;
    }

    /**
     * Establece la ciudad de ubicación.
     * @param ciudad Nombre de la ciudad.
     */
    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    /**
     * Obtiene la descripción detallada.
     * @return Texto de la descripción.
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Actualiza la descripción del alojamiento.
     * @param descripcion Nuevo texto descriptivo.
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Obtiene la capacidad máxima de huéspedes.
     * @return Número de personas.
     */
    public int getCapacidad() {
        return capacidad;
    }

    /**
     * Define la capacidad máxima de huéspedes.
     * @param capacidad Número entero mayor que 0.
     */
    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    /**
     * Obtiene el precio por noche.
     * @return Valor monetario preciso.
     */
    public BigDecimal getPrecio() {
        return precio;
    }

    /**
     * Establece el precio por noche.
     * @param precio Valor monetario en {@link BigDecimal}.
     */
    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    /**
     * Obtiene la URL de la imagen principal.
     * @return Cadena con la URL o path de la imagen.
     */
    public String getFotoUrl() {
        return fotoUrl;
    }

    /**
     * Actualiza la URL de la imagen principal.
     * @param fotoUrl Nueva URL.
     */
    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    /**
     * Obtiene la distancia al centro de la ciudad.
     * @return Distancia (usualmente en km/m según la lógica de negocio).
     */
    public BigDecimal getDistanciaCentro() {
        return distanciaCentro;
    }

    /**
     * Establece la distancia al centro.
     * @param distanciaCentro Valor numérico de la distancia.
     */
    public void setDistanciaCentro(BigDecimal distanciaCentro) {
        this.distanciaCentro = distanciaCentro;
    }

    /**
     * Obtiene la valoración media actual.
     * @return Puntuación media (puede ser null).
     */
    public Double getValoracionMedia() {
        return valoracionMedia;
    }

    /**
     * Actualiza la valoración media del alojamiento.
     * @param valoracionMedia Nueva puntuación.
     */
    public void setValoracionMedia(Double valoracionMedia) {
        this.valoracionMedia = valoracionMedia;
    }
}