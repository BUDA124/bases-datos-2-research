package tec.curso.prototype.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tec.curso.prototype.dto.ProductSaleEventDto;
import tec.curso.prototype.dto.TicketSaleEventDto;
import tec.curso.prototype.repositories.DruidRepository;
import tec.curso.prototype.store.InMemoryDataStore;
import tec.curso.prototype.store.Pelicula;
import tec.curso.prototype.store.Producto;

@Service
public class SalesAreaService {

    private final DruidRepository druidRepository;
    private final InMemoryDataStore dataStore;

    @Autowired
    public SalesAreaService(DruidRepository druidRepository, InMemoryDataStore dataStore) {
        this.druidRepository = druidRepository;
        this.dataStore = dataStore;
    }

    /**
     * Orquesta el proceso completo de vender tiquetes para una película.
     * @param tituloPelicula El título de la película.
     * @param cantidad La cantidad de tiquetes a vender.
     * @return true si la venta fue exitosa, false si la película no existe.
     */
    public boolean registrarVentaTiquete(String tituloPelicula, int cantidad) {
        Pelicula pelicula = dataStore.findMovieByTitle(tituloPelicula);
        if (pelicula == null) {
            System.err.println("Intento de venta para película no existente: " + tituloPelicula);
            return false;
        }

        // 1. Crear el evento de venta para Druid.
        TicketSaleEventDto event = new TicketSaleEventDto(
                tituloPelicula,
                cantidad,
                pelicula.getPrecioEntrada()
        );

        // 2. Enviar el evento a Druid para análisis.
        druidRepository.ingestTicketSale(event);

        System.out.println("Evento de venta de tiquete registrado en Druid para: " + tituloPelicula);
        return true;
    }

    /**
     * Orquesta el proceso completo de vender un producto de dulcería.
     * Primero, valida y actualiza el estado en memoria.
     * Si tiene éxito, registra el evento en Druid.
     * @param nombreProducto El nombre del producto.
     * @param cantidad La cantidad a vender.
     * @return true si la venta fue exitosa, false si no hay stock.
     */
    public boolean registrarVentaProducto(String nombreProducto, int cantidad) {
        Producto producto = dataStore.findProductByName(nombreProducto);
        if (producto == null || producto.getUnidadesDisponibles() < cantidad) {
            return false;
        }

        // 1. Actualizar el estado transaccional en memoria (¡Paso CRÍTICO!).
        producto.setUnidadesDisponibles(producto.getUnidadesDisponibles() - cantidad);
        dataStore.updadteExistingProduct(producto);

        // 2. Crear el evento de venta para Druid.
        ProductSaleEventDto event = new ProductSaleEventDto(
                nombreProducto,
                cantidad,
                producto.getPrecio()
        );

        // 3. Enviar el evento a Druid para análisis.
        druidRepository.ingestProductSale(event);

        return true;
    }
}