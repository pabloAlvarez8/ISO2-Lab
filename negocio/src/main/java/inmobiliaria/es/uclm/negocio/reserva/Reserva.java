package inmobiliaria.es.uclm.negocio.reserva;

import inmobiliaria.es.uclm.negocio.alojamiento.Alojamiento;
import inmobiliaria.es.uclm.negocio.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@Entity
@Table(name = "reserva") // <--- Recomendado: Nombre explícito en minúsculas
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con User (Tabla 'usuario')
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false) // 'nullable = false' evita reservas sin inquilino
    private User inquilino;

    // Relación con Alojamiento (Tabla 'inmueble')
    @ManyToOne
    @JoinColumn(name = "alojamiento_id", nullable = false) // 'nullable = false' evita reservas sin casa
    private Alojamiento alojamiento;

    @Column(nullable = false)
    private LocalDate fechaEntrada;

    @Column(nullable = false)
    private LocalDate fechaSalida;
    
    // Derby no tiene ENUM, así que lo dejamos como String. 
    // Valores esperados: "PENDIENTE", "ACEPTADA", "RECHAZADA"
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