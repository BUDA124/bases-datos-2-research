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

    /**
     * El método @PostConstruct se ejecuta una vez después de que el bean ha sido creado.
     * Ahora cargará los datos desde los archivos JSON o inicializará con datos por defecto si los archivos no existen.
     */
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

    public void createNerProduct(Producto producto) {
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

    private void inicializarPeliculasPorDefecto() {
        Pelicula p1 = new Pelicula("Galaxy Raiders 4", 7.50);
        Pelicula p2 = new Pelicula("Amores en París", 6.00);
        Pelicula p3 = new Pelicula("La Casa del Terror", 8.00);
        carteleraPeliculas.put(p1.getTitulo(), p1);
        carteleraPeliculas.put(p2.getTitulo(), p2);
        carteleraPeliculas.put(p3.getTitulo(), p3);
        guardarPeliculas(); // Guarda los datos iniciales si no existían
    }

    private void inicializarProductosPorDefecto() {
        Producto pr1 = new Producto("Palomitas Grandes", 400, 40, 10.0);
        Producto pr2 = new Producto("Refresco Mediano", 1000, 100, 4.0);
        Producto pr3 = new Producto("Nachos con Queso", 100, 10, 8.0);
        inventarioProductos.put(pr1.getNombre(), pr1);
        inventarioProductos.put(pr2.getNombre(), pr2);
        inventarioProductos.put(pr3.getNombre(), pr3);
        guardarProductos(); // Guarda los datos iniciales si no existían
    }
}