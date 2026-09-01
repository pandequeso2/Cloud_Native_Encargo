package cl.duoc.trabajos_service.controller;

import cl.duoc.trabajos_service.model.Trabajo;
import cl.duoc.trabajos_service.service.TrabajoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

@Tag(name = "Trabajos", description = "Operaciones relacionadas con la gestión de trabajos")
@RestController
@RequestMapping("api/v1/trabajos")
public class TrabajoController {

    @Autowired
    private TrabajoService trabajoService;

    @Operation(summary = "Obtiene todos los trabajos", description = "Retorna la lista de todos los trabajos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aprobado"),
            @ApiResponse(responseCode = "404", description = "No se encontraron trabajos")
    })
    @GetMapping
    public ResponseEntity<?> obtenerTrabajos() {
        List<Trabajo> trabajos = trabajoService.obtenerTrabajos();
        if (trabajos.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay trabajos");
        }
        return ResponseEntity.ok(trabajos);
    }

    @Operation(summary = "Obtiene un trabajo por su id ", description = "Retorna la informacion de un trabajo específico por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aprobado"),
            @ApiResponse(responseCode = "404", description = "Trabajo no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerTrabajo(@PathVariable Integer id){
        Optional<Trabajo> trabajoOptional = trabajoService.buscarTrabajoPorId(id);

        if (trabajoOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No existe un trabajo con el ID " + id);
        }

        Trabajo trabajo = trabajoOptional.get();
        EntityModel<Trabajo> model = EntityModel.of(trabajo);

        model.add(
                linkTo(
                        methodOn(TrabajoController.class).obtenerTrabajo(id)
                ).withSelfRel()
        );

        model.add(
                Link.of("http://localhost:8090" +
                        "/api/v1/trabajos" + id, "eliminar")
        );

        model.add(
                linkTo(
                        methodOn(TrabajoController.class)
                                .obtenerTrabajos()
                ).withRel("todos-los trabajos")
        );
        return ResponseEntity.ok(model);
    }

    @Operation(summary = "Crea un nuevo trabajo", description = "Registra un trabajo nuevo en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<Trabajo> crearTrabajo(@RequestBody @Valid Trabajo trabajo) {
        return ResponseEntity.ok(trabajoService.guardarTrabajo(trabajo));
    }

    @Operation(summary = "Actualiza un trabajo", description = "Actualiza los datos de un trabajo existente por su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aprobado"),
            @ApiResponse(responseCode = "404", description = "Trabajo no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable int id, @RequestBody Trabajo trabajo) {
        try {
            return ResponseEntity.ok(trabajoService.actualizarTrabajo(id, trabajo));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe trabajo con el id " +id);
        }
    }

    @Operation(summary = "Elimina un trabajo", description = "Elimina un trabajo existente por su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Eliminado"),
            @ApiResponse(responseCode = "404", description = "Trabajo no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        try {
            trabajoService.eliminarTrabajo(id);
            return ResponseEntity.ok("Trabajo eliminado con exito");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no existe un trabajo con el id " + id);
        }
    }

    @Operation(summary = "Obtiene trabajos por grupo", description = "Retorna el trabajo asociado a un grupo específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aprobado"),
            @ApiResponse(responseCode = "404", description = "Grupo no encontrado")
    })
    @GetMapping("/grupo/{idGrupo}")
    public ResponseEntity<Trabajo> obtenerPorGrupo(@PathVariable Integer idGrupo) {
        return ResponseEntity.ok(trabajoService.obtenerPorGrupo(idGrupo));
    }
}