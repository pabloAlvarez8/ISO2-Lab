package inmobiliaria.es.uclm.negocio.reserva;

import inmobiliaria.es.uclm.negocio.alojamiento.Alojamiento;
import inmobiliaria.es.uclm.negocio.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter // Lombok crea los setters para TODOS los campos
@Getter // Lombok crea los getters para TODOS los campos
@Entity
@Table(name = "reserva")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private User inquilino;

    @ManyToOne
    @JoinColumn(name = "alojamiento_id", nullable = false)
    private Alojamiento alojamiento;

    @Column(nullable = false)
    private LocalDate fechaEntrada;

    @Column(nullable = false)
    private LocalDate fechaSalida;

    @Column(nullable = false)
    private double precioTotal;

    private String estado;

    // ¡AQUÍ YA NO ESCRIBES NADA MÁS!
    // Lombok se encarga del resto.
}