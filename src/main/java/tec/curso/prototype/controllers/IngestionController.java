package tec.curso.prototype.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tec.curso.prototype.dto.VentaDto;
import tec.curso.prototype.dto.VentaFuncionDto;
import tec.curso.prototype.dto.VentaPeliculaDto;
import tec.curso.prototype.dto.VentaProductoDto;
import tec.curso.prototype.services.FinancialService;
import tec.curso.prototype.services.InventoryService;
import tec.curso.prototype.services.MovieSalesService;
import tec.curso.prototype.services.ShowtimeService;

/**
 * Controlador REST para manejar las simulaciones de ventas desde el dashboard.
 * Cada endpoint corresponde a un módulo de simulación y utiliza un servicio de negocio especializado.
 */
@RestController
@RequestMapping("/api/simulacion")
public class IngestionController {

    // Inyección de los servicios de negocio especializados en lugar de uno solo.
    private final FinancialService financialService;
    private final MovieSalesService movieSalesService;
    private final ShowtimeService showtimeService;
    private final InventoryService inventoryService;

    @Autowired
    public IngestionController(
            FinancialService financialService,
            MovieSalesService movieSalesService,
            ShowtimeService showtimeService,
            InventoryService inventoryService) {
        this.financialService = financialService;
        this.movieSalesService = movieSalesService;
        this.showtimeService = showtimeService;
        this.inventoryService = inventoryService;
    }

    // Endpoint para "Pulso Financiero del Día"
    @PostMapping("/venta-financiera")
    public ResponseEntity<String> registrarVentaFinanciera(@RequestBody VentaDto venta) {
        // Llama al servicio dedicado a la lógica financiera.
        financialService.registrarVentaFinanciera(venta);
        return ResponseEntity.ok("Venta financiera registrada con éxito.");
    }

    // Endpoint para "El Poder de Venta de las Películas"
    @PostMapping("/venta-por-pelicula")
    public ResponseEntity<String> registrarVentaPorPelicula(@RequestBody VentaPeliculaDto venta) {
        // Llama al servicio que maneja las ventas asociadas a películas.
        movieSalesService.registrarVentaAsociadaAPelicula(venta);
        return ResponseEntity.ok("Venta por película registrada con éxito.");
    }

    // Endpoint para "Ocupación y Oportunidad por Función"
    @PostMapping("/venta-entradas")
    public ResponseEntity<String> registrarVentaEntradas(@RequestBody VentaFuncionDto venta) {
        // Llama al servicio responsable de la lógica de ocupación de funciones.
        showtimeService.registrarVentaDeEntradas(venta);
        return ResponseEntity.ok("Venta de entradas registrada con éxito.");
    }

    // Endpoint para "Inventario Inteligente"
    @PostMapping("/venta-producto")
    public ResponseEntity<String> registrarVentaProducto(@RequestBody VentaProductoDto venta) {
        // Llama al servicio que gestiona el inventario.
        inventoryService.registrarVentaDeProducto(venta);
        return ResponseEntity.ok("Venta de producto registrada con éxito.");
    }
}