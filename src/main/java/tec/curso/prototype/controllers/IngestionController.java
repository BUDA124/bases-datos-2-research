package tec.curso.prototype.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tec.curso.prototype.dto.*;
import tec.curso.prototype.services.IngestionService;

@RestController
@RequestMapping("/api/simulacion")
public class IngestionController {

    private final IngestionService ingestionService;

    @Autowired
    public IngestionController(IngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    // Endpoint para "Pulso Financiero del Día"
    @PostMapping("/venta-financiera")
    public ResponseEntity<String> registrarVentaFinanciera(@RequestBody VentaDto venta) {
        ingestionService.registrarVentaFinanciera(venta);
        return ResponseEntity.ok("Venta financiera registrada con éxito.");
    }

    // Endpoint para "El Poder de Venta de las Películas"
    @PostMapping("/venta-por-pelicula")
    public ResponseEntity<String> registrarVentaPorPelicula(@RequestBody VentaPeliculaDto venta) {
        ingestionService.registrarVentaAsociadaAPelicula(venta);
        return ResponseEntity.ok("Venta por película registrada con éxito.");
    }

    // Endpoint para "Ocupación y Oportunidad por Función"
    @PostMapping("/venta-entradas")
    public ResponseEntity<String> registrarVentaEntradas(@RequestBody VentaFuncionDto venta) {
        ingestionService.registrarVentaDeEntradas(venta);
        return ResponseEntity.ok("Venta de entradas registrada con éxito.");
    }

    // Endpoint para "Inventario Inteligente"
    @PostMapping("/venta-producto")
    public ResponseEntity<String> registrarVentaProducto(@RequestBody VentaProductoDto venta) {
        ingestionService.registrarVentaDeProducto(venta);
        return ResponseEntity.ok("Venta de producto registrada con éxito.");
    }
}