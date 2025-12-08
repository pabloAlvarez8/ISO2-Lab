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

    // Getters y Setters
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

}