package alquiler.demo.entity;

public class Vivienda {
    private Long id;
    private String direccion;
    private Double precioMensual;

    public Vivienda() {}

    public Vivienda(Long id, String direccion, Double precioMensual) {
        this.id = id;
        this.direccion = direccion;
        this.precioMensual = precioMensual;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public Double getPrecioMensual() { return precioMensual; }
    public void setPrecioMensual(Double precioMensual) { this.precioMensual = precioMensual; }
}

