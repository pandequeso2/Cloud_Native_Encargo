package cl.duoc.comentarios_service.controller;

import cl.duoc.comentarios_service.model.Comentario;
import cl.duoc.comentarios_service.service.ComentarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/comentarios")
public class ComentarioController {

    @Autowired
    private ComentarioService service;

    @GetMapping
    public List<Comentario> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comentario> obtenerPorId(@PathVariable int id) {
        Optional<Comentario> comentario = service.buscarPorId(id);
        if (comentario.isPresent()) {
            return ResponseEntity.ok(comentario.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public Comentario crear(@RequestBody Comentario comentario) {
        return service.guardar(comentario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comentario> actualizar(@PathVariable int id, @RequestBody Comentario comentario) {
        try {
            return ResponseEntity.ok(service.actualizar(id, comentario));
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