package tec.curso.prototype.store;

public class Producto {
    private String nombre;
    private int unidadesDisponibles;
    private int minimoSugerido;
    private double precio;

    public Producto() {
    }

    public Producto(String nombre, int unidadesDisponibles, int minimoSugerido, double precio) {
        this.nombre = nombre;
        this.unidadesDisponibles = unidadesDisponibles;
        this.minimoSugerido = minimoSugerido;
        this.precio = precio;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getMinimoSugerido() {
        return minimoSugerido;
    }

    public void setMinimoSugerido(int minimoSugerido) {
        this.minimoSugerido = minimoSugerido;
    }

    public int getUnidadesDisponibles() {
        return unidadesDisponibles;
    }

    public void setUnidadesDisponibles(int unidadesDisponibles) {
        this.unidadesDisponibles = unidadesDisponibles;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}