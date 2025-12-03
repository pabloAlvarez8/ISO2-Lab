package inmobiliaria.es.uclm.negocio.reserva;

import inmobiliaria.es.uclm.negocio.alojamiento.Alojamiento;
import inmobiliaria.es.uclm.negocio.user.User;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quién hace la reserva (Inquilino)
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private User inquilino;

    // Qué casa se reserva
    @ManyToOne
    @JoinColumn(name = "alojamiento_id")
    private Alojamiento alojamiento;

    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;
    
    // Estado: "PENDIENTE", "ACEPTADA", "RECHAZADA"
    private String estado; 

    // Getters y Setters...
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