package tec.curso.prototype.store;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import tec.curso.prototype.store.Pelicula;
import tec.curso.prototype.store.Producto;

import jakarta.annotation.PostConstruct;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class InMemoryDataStore {

    private final Map<String, Producto> inventarioProductos = new ConcurrentHashMap<>();
    private final Map<String, Pelicula> carteleraPeliculas = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final File peliculasFile = new File("peliculas.json");
    private final File productosFile = new File("productos.json");

    @PostConstruct
    public void init() {
        cargarPeliculas();
        cargarProductos();

        if (carteleraPeliculas.isEmpty()) {
            inicializarPeliculasPorDefecto();
        }

        if (inventarioProductos.isEmpty()) {
            inicializarProductosPorDefecto();
        }
    }

    // --- Métodos para interactuar con el inventario ---

    public List<Producto> findAllProducts() {
        return new ArrayList<>(inventarioProductos.values());
    }

    public Producto findProductByName(String name) {
        return inventarioProductos.get(name);
    }

    public void createNewProduct(Producto producto) {
        if (inventarioProductos.containsKey(producto.getNombre())) {
            throw new IllegalArgumentException("El producto ya existe en el sistema");
        }
        inventarioProductos.put(producto.getNombre(), producto);
        guardarProductos(); // Guardar cambios en el archivo
    }

    public void updadteExistingProduct(Producto producto) {
        if (!inventarioProductos.containsKey(producto.getNombre())) {
            throw new IllegalArgumentException("El producto no existe en el sistema");
        }
        inventarioProductos.put(producto.getNombre(), producto);
        guardarProductos(); // Guardar cambios en el archivo
    }

    // --- Métodos para interactuar con las películas ---

    public List<Pelicula> findAllMovies() {
        return new ArrayList<>(carteleraPeliculas.values());
    }

    public Pelicula findMovieByTitle(String title) {
        return carteleraPeliculas.get(title);
    }

    // --- Nuevos métodos para la persistencia ---

    private void cargarPeliculas() {
        if (peliculasFile.exists()) {
            try {
                List<Pelicula> listaPeliculas = objectMapper.readValue(peliculasFile, new TypeReference<List<Pelicula>>() {});
                carteleraPeliculas.putAll(listaPeliculas.stream().collect(Collectors.toMap(Pelicula::getTitulo, Function.identity())));
            } catch (IOException e) {
                // Manejar la excepción, por ejemplo, logueando el error.
                e.printStackTrace();
            }
        }
    }

    private void guardarPeliculas() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(peliculasFile, findAllMovies());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cargarProductos() {
        if (productosFile.exists()) {
            try {
                List<Producto> listaProductos = objectMapper.readValue(productosFile, new TypeReference<List<Producto>>() {});
                inventarioProductos.putAll(listaProductos.stream().collect(Collectors.toMap(Producto::getNombre, Function.identity())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void guardarProductos() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(productosFile, findAllProducts());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- Métodos de inicialización por defecto ---

    /**
     * Inicializa la cartelera con una selección variada de películas por defecto.
     * Esta selección incluye diferentes géneros para atraer a un público más amplio.
     */
    private void inicializarPeliculasPorDefecto() {
        // Películas de Acción y Aventura
        Pelicula p1 = new Pelicula("Duna: Parte Dos", 9.50);
        Pelicula p2 = new Pelicula("TRON: Ares", 8.75);
        Pelicula p3 = new Pelicula("Gladiator 2", 9.00);

        // Películas de Animación
        Pelicula p4 = new Pelicula("IntensaMente 2", 7.80);
        Pelicula p5 = new Pelicula("Mi Villano Favorito 4", 7.50);
        Pelicula p6 = new Pelicula("Spider-Man: Beyond the Spider-Verse", 8.50);

        // Películas de Terror y Suspenso
        Pelicula p7 = new Pelicula("Un Lugar en Silencio: Día Uno", 8.20);
        Pelicula p8 = new Pelicula("Alien: Romulus", 8.00);

        // Películas de Comedia y Drama
        Pelicula p9 = new Pelicula("Amigos Imaginarios", 6.50);
        Pelicula p10 = new Pelicula("El Reino del Planeta de los Simios", 8.90);

        carteleraPeliculas.put(p1.getTitulo(), p1);
        carteleraPeliculas.put(p2.getTitulo(), p2);
        carteleraPeliculas.put(p3.getTitulo(), p3);
        carteleraPeliculas.put(p4.getTitulo(), p4);
        carteleraPeliculas.put(p5.getTitulo(), p5);
        carteleraPeliculas.put(p6.getTitulo(), p6);
        carteleraPeliculas.put(p7.getTitulo(), p7);
        carteleraPeliculas.put(p8.getTitulo(), p8);
        carteleraPeliculas.put(p9.getTitulo(), p9);
        carteleraPeliculas.put(p10.getTitulo(), p10);

        guardarPeliculas();
    }

    /**
     * Inicializa el inventario con una variedad de productos de dulcería.
     * Incluye diferentes tamaños y tipos de snacks y bebidas populares.
     */
    private void inicializarProductosPorDefecto() {
        // Palomitas
        Producto pr1 = new Producto("Palomitas Chicas", 500, 50, 5.50);
        Producto pr2 = new Producto("Palomitas Medianas", 400, 40, 7.00);
        Producto pr3 = new Producto("Palomitas Grandes", 300, 30, 8.50);

        // Bebidas
        Producto pr5 = new Producto("Refresco Pequeño", 1000, 100, 3.50);
        Producto pr6 = new Producto("Refresco Mediano", 1000, 100, 4.50);
        Producto pr7 = new Producto("Refresco Grande", 800, 80, 5.50);
        Producto pr8 = new Producto("Agua Embotellada", 1200, 120, 2.50);
        Producto pr9 = new Producto("ICEE Grande", 400, 40, 6.00);

        // Snacks y Dulces
        Producto pr10 = new Producto("Nachos con Queso", 150, 15, 7.80);
        Producto pr11 = new Producto("Nachos con Queso y Jalapeños", 100, 10, 8.50);
        Producto pr12 = new Producto("Hot Dog Clásico", 200, 20, 5.00);
        Producto pr13 = new Producto("M&Ms de Chocolate", 300, 30, 4.00);
        Producto pr14 = new Producto("Skittles", 300, 30, 4.00);
        Producto pr15 = new Producto("Chocolates Reese's", 250, 25, 4.25);

        inventarioProductos.put(pr1.getNombre(), pr1);
        inventarioProductos.put(pr2.getNombre(), pr2);
        inventarioProductos.put(pr3.getNombre(), pr3);
        inventarioProductos.put(pr5.getNombre(), pr5);
        inventarioProductos.put(pr6.getNombre(), pr6);
        inventarioProductos.put(pr7.getNombre(), pr7);
        inventarioProductos.put(pr8.getNombre(), pr8);
        inventarioProductos.put(pr9.getNombre(), pr9);
        inventarioProductos.put(pr10.getNombre(), pr10);
        inventarioProductos.put(pr11.getNombre(), pr11);
        inventarioProductos.put(pr12.getNombre(), pr12);
        inventarioProductos.put(pr13.getNombre(), pr13);
        inventarioProductos.put(pr14.getNombre(), pr14);
        inventarioProductos.put(pr15.getNombre(), pr15);

        guardarProductos();
    }
}