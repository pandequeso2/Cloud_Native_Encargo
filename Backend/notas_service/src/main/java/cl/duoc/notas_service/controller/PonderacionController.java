package cl.duoc.notas_service.controller;

import cl.duoc.notas_service.model.Nota;
import cl.duoc.notas_service.model.Ponderacion;
import cl.duoc.notas_service.service.PonderacionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.EntityModel;

import java.util.List;
import java.util.Optional;

@Tag(name = "Ponderaciones", description = "Operaciones relacionadas con la gestión de ponderaciones")
@RestController
@RequestMapping("/api/v1/ponderaciones")
public class PonderacionController {

    @Autowired
    private PonderacionService ponderacionService;

    @Operation(summary = "Obtiene una ponderacion por su id ", description = "Retorna la informacios de una ponderacion específica por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aprobado"),
            @ApiResponse(responseCode = "404", description = "Ponderacion no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPonderacion(@PathVariable Integer id){
        Optional<Ponderacion> ponderacionOptional = ponderacionService.buscarPonderacionPorId(id);

        if (ponderacionOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No existe una ponderacion con el ID " + id);
        }

        Ponderacion ponderacion = ponderacionOptional.get();
        EntityModel<Ponderacion> model = EntityModel.of(ponderacion);

        model.add(
                linkTo(
                        methodOn(PonderacionController.class).obtenerPonderacion(id)
                ).withSelfRel()
        );

        model.add(
                Link.of("http://localhost:8083" +
                        "/api/v1/ponderaciones" + id, "eliminar")
        );

        model.add(
                linkTo(
                        methodOn(PonderacionController.class)
                                .obtenerPonderaciones()
                ).withRel("todos-los ponderaciones")
        );
        return ResponseEntity.ok(model);
    }

    @Operation(summary = "Obtiene todas las ponderaciones", description = "Retorna la lista de todas las ponderaciones")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aprobado"),
            @ApiResponse(responseCode = "404", description = "No se encontraron ponderaciones")
    })
    @GetMapping
    public ResponseEntity<?> obtenerPonderaciones() {
        List<Ponderacion> ponderaciones = ponderacionService.obtenerPonderacion();
        if (ponderaciones.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay ponderaciones");
        }
        return ResponseEntity.ok(ponderaciones);
    }

    @Operation(summary = "Crea una nueva ponderacion", description = "Registra una ponderacion nueva en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<Ponderacion> crearPonderacion(@RequestBody @Valid Ponderacion ponderacion) {
        return ResponseEntity.ok(ponderacionService.guardarPonderacion(ponderacion));
    }

    @Operation(summary = "Actualiza una ponderacion", description = "Actualiza los datos de una ponderacion existente por su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aprobado"),
            @ApiResponse(responseCode = "404", description = "Ponderacion no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable int id, @RequestBody Ponderacion ponderacion) {
        try {
            return ResponseEntity.ok(ponderacionService.actualizarPonderacion(id, ponderacion));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe una ponderacion con el id " +id);
        }
    }

    @Operation(summary = "Elimina una ponderacion", description = "Elimina una ponderacion existente por su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Eliminado"),
            @ApiResponse(responseCode = "404", description = "Ponderacion no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        try {
            ponderacionService.eliminarPonderacion(id);
            return ResponseEntity.ok("ponderacion eliminada con exito");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no existe una ponderacion con el id " + id);
        }
    }
}