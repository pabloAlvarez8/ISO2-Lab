package inmobiliaria.es.uclm.negocio.reserva;

import inmobiliaria.es.uclm.negocio.alojamiento.Alojamiento;
import inmobiliaria.es.uclm.negocio.user.User;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "reserva")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // inquilino_id
    @ManyToOne
    @JoinColumn(name = "inquilino_id", nullable = false) 
    private User inquilino;

    // inmueble_id
    @ManyToOne
    @JoinColumn(name = "inmueble_id", nullable = false) 
    private Alojamiento alojamiento;

    // Mapeamos las fechas a los nombres de la BD ---
    
    @Column(name = "fecha_inicio", nullable = false) 
    private LocalDate fechaEntrada;

    @Column(name = "fecha_fin", nullable = false)    
    private LocalDate fechaSalida;
   
    private String estado; 

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getInquilino() { return inquilino; }
    public void setInquilino(User inquilino) { this.inquilino = inquilino; }
    public Alojamiento getAlojamiento() { return alojamiento; }
    public void setAlojamiento(Alojamiento alojamiento) { this.alojamiento = alojamiento; }
    public LocalDate getFechaEntrada() { return fechaEntrada; }
    public void setFechaEntrada(LocalDate fechaEntrada) { this.fechaEntrada = fechaEntrada; }
    public LocalDate getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(LocalDate fechaSalida) { this.fechaSalida = fechaSalida; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}