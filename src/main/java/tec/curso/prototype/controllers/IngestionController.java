package tec.curso.prototype.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tec.curso.prototype.services.SalesAreaService;

@RestController
@RequestMapping("/api/simulacion") // Cambiamos el nombre para que sea más claro
public class IngestionController {

    private final SalesAreaService salesAreaService;

    @Autowired
    public IngestionController(SalesAreaService salesAreaService) {
        this.salesAreaService = salesAreaService;
    }

    // Endpoint para la venta de tiquetes (Taquilla)
    @PostMapping("/ticket-sale")
    public ResponseEntity<String> simularVentaTaquilla(@RequestBody TicketSaleRequest request) {
        // Llamamos al método correcto del servicio con los datos simples
        boolean exito = salesAreaService.registrarVentaTiquete(
                request.getTituloPelicula(),
                request.getCantidad()
        );

        if (exito) {
            return ResponseEntity.ok("Simulación de Venta de Taquilla procesada con éxito.");
        } else {
            return ResponseEntity.badRequest().body("No se pudo procesar la venta (película no encontrada).");
        }
    }

    // Endpoint para la venta de productos (Dulcería)
    @PostMapping("/product-sale")
    public ResponseEntity<String> simularVentaDulceria(@RequestBody ProductSaleRequest request) {
        // Llamamos al método correcto del servicio
        boolean exito = salesAreaService.registrarVentaProducto(
                request.getNombreProducto(),
                request.getCantidad()
        );

        if (exito) {
            return ResponseEntity.ok("Simulación de Venta de Producto procesada con éxito.");
        } else {
            return ResponseEntity.badRequest().body("No se pudo procesar la venta (stock insuficiente o producto no encontrado).");
        }
    }

    // --- Clases internas para las solicitudes ---
    public static class TicketSaleRequest {
        private String tituloPelicula;
        private int cantidad;

        public String getTituloPelicula() {
            return tituloPelicula;
        }

        public void setTituloPelicula(String tituloPelicula) {
            this.tituloPelicula = tituloPelicula;
        }

        public int getCantidad() {
            return cantidad;
        }

        public void setCantidad(int cantidad) {
            this.cantidad = cantidad;
        }
    }

    public static class ProductSaleRequest {
        private String nombreProducto;
        private int cantidad;

        public String getNombreProducto() {
            return nombreProducto;
        }

        public void setNombreProducto(String nombreProducto) {
            this.nombreProducto = nombreProducto;
        }

        public int getCantidad() {
            return cantidad;
        }

        public void setCantidad(int cantidad) {
            this.cantidad = cantidad;
        }
    }
}