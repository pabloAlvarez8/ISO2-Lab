package alquiler.negocio.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Disponibilidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;
    private boolean disponible;

    // Relación con Alojamiento
    @ManyToOne
    @JoinColumn(name = "alojamiento_id")
    private Alojamiento alojamiento;

    public Disponibilidad() {}

    public Disponibilidad(LocalDate fecha, boolean disponible, Alojamiento alojamiento) {
        this.fecha = fecha;
        this.disponible = disponible;
        this.alojamiento = alojamiento;
    }

    // Getters y setters
    public Long getId() { return id; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
    public Alojamiento getAlojamiento() { return alojamiento; }
    public void setAlojamiento(Alojamiento alojamiento) { this.alojamiento = alojamiento; }
}
