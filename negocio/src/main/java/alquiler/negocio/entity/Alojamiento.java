package alquiler.negocio.entity;

import jakarta.persistence.*;
import java.util.*;

@Entity
public class Alojamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type; // "hotel" o "apartamento"
    private String ciudad;
    private String title;
    private double price;
    private double rating;
    private String img;
    private double distance;
    private int capacity;

    //Relación con disponibilidad
    @OneToMany(mappedBy = "alojamiento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Disponibilidad> calendario = new ArrayList<>();

    //Constructores
    public Alojamiento() {}

    public Alojamiento(String type, String ciudad, String title, double price, double rating, String img, double distance, int capacity) {
        this.type = type;
        this.ciudad = ciudad;
        this.title = title;
        this.price = price;
        this.rating = rating;
        this.img = img;
        this.distance = distance;
        this.capacity = capacity;
    }

    //Getters y setters
    public Long getId() { return id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public String getImg() { return img; }
    public void setImg(String img) { this.img = img; }
    public double getDistance() { return distance; }
    public void setDistance(double distance) { this.distance = distance; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public List<Disponibilidad> getCalendario() { return calendario; }
    public void setCalendario(List<Disponibilidad> calendario) { this.calendario = calendario; }
}
