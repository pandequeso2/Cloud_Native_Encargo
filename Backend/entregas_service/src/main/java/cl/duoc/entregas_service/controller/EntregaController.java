package cl.duoc.entregas_service.controller;

import cl.duoc.entregas_service.model.Entrega;
import cl.duoc.entregas_service.service.EntregaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/entregas")
public class  EntregaController {

    @Autowired
    private EntregaService service;

    @GetMapping
    public List<Entrega> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Entrega> obtenerPorId(@PathVariable int id) {
        Optional<Entrega> entrega = service.buscarPorId(id);
        if (entrega.isPresent()) {
            return ResponseEntity.ok(entrega.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public Entrega crear(@RequestBody Entrega entrega) {
        return service.guardar(entrega);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Entrega> actualizar(@PathVariable int id, @RequestBody Entrega entrega) {
        try {
            return ResponseEntity.ok(service.actualizar(id, entrega));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {
        try {
            service.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}