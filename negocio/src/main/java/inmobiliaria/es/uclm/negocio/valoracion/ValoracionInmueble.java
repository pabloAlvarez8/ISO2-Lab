package inmobiliaria.es.uclm.negocio.valoracion;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import inmobiliaria.es.uclm.negocio.alojamiento.Alojamiento;
import inmobiliaria.es.uclm.negocio.user.User;

@Entity
@Table(name = "valoracion_inmueble")
public class ValoracionInmueble {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "inmueble_id", nullable = false)
    @JsonIgnore 
    private Alojamiento inmueble;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnoreProperties({"contrasena", "reservas", "valoracionesRealizadas", "password"}) 
    private User usuario;

    private Integer puntuacion;
    @Column(length = 3000)
    private String comentario;
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public ValoracionInmueble() {}
    public ValoracionInmueble(Alojamiento inmueble, User usuario, Integer puntuacion, String comentario) {
        this.inmueble = inmueble;
        this.usuario = usuario;
        this.puntuacion = puntuacion;
        this.comentario = comentario;
    }

    // Getters y Setters básicos
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Alojamiento getInmueble() { return inmueble; }
    public void setInmueble(Alojamiento inmueble) { this.inmueble = inmueble; }
    public User getUsuario() { return usuario; }
    public void setUsuario(User usuario) { this.usuario = usuario; }
    public Integer getPuntuacion() { return puntuacion; }
    public void setPuntuacion(Integer puntuacion) { this.puntuacion = puntuacion; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}