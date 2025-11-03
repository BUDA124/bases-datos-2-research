package tec.curso.prototype.store;

public class Pelicula {
    private String titulo;
    private double precioEntrada;

    public Pelicula() {
    }

    public Pelicula(String titulo, double precioEntrada) {
        this.titulo = titulo;
        this.precioEntrada = precioEntrada;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public double getPrecioEntrada() {
        return precioEntrada;
    }

    public void setPrecioEntrada(double precioEntrada) {
        this.precioEntrada = precioEntrada;
    }
}