package cl.duoc.asignaturas_service.controller;

import cl.duoc.asignaturas_service.dto.AsignaturaDTO;
import cl.duoc.asignaturas_service.dto.AsignaturaSeccionDTO;
import cl.duoc.asignaturas_service.model.Asignatura;
import cl.duoc.asignaturas_service.service.AsignaturaService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.Link;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;


import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Asignaturas", description = "Operaciones relacionadas con la gestión de asignaturas")
@RestController
@RequestMapping("/api/v1/asignaturas")
public class AsignaturaController {

    @Autowired
    private AsignaturaService asignaturaService;

    @Operation(summary = "Obtiene todas las asignaturas", description = "Retorna la lista de todas las asignaturas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aprobado"),
            @ApiResponse(responseCode = "204", description = "No hay asignaturas registradas")
    })
    @GetMapping
    public ResponseEntity<List<Asignatura>> obtenerAsignaturas(){
        List<Asignatura> asignaturas = asignaturaService.obtenerAsignaturas();

        if (asignaturas.isEmpty())
            return ResponseEntity.noContent().build();
        else
            return ResponseEntity.ok(asignaturas);
    }

    @Operation (summary = "Obtiene una asignatura por su id ", description = "Retorna la informacion de una asignatura específico por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aprobado"),
            @ApiResponse(responseCode = "404", description = "Asignatura no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerAsignatura(@PathVariable Integer id){
        Optional<Asignatura> asignaturaOptional = asignaturaService.buscarAsignaturaPorId(id);

        if (asignaturaOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No existe una asignatura con el ID " + id);
        }

        Asignatura asignatura = asignaturaOptional.get();
        EntityModel<Asignatura> model = EntityModel.of(asignatura);

        model.add(
                linkTo(
                        methodOn(AsignaturaController.class).obtenerAsignatura(id)
                ).withSelfRel()
        );

        model.add(
                Link.of("http://localhost:8084" +
                        "/api/v1/asignaturas" + id, "eliminar")
        );

        model.add(
                linkTo(
                        methodOn(AsignaturaController.class)
                                .obtenerAsignaturas()
                ).withRel("todos-las asignaturas")
        );
        return ResponseEntity.ok(model);
    }

    @Operation(summary = "Crea una nueva asignatura", description = "Registra una asignatura nueva en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<Asignatura> crearAsignatura(@RequestBody @Valid Asignatura asignatura) {
        return ResponseEntity.ok(asignaturaService.guardarAsignatura(asignatura));
    }

    @Operation(summary = "Actualiza una asignatura", description = "Actualiza los datos de una asignatura existente por su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aprobado"),
            @ApiResponse(responseCode = "404", description = "Asignatura no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable int id, @RequestBody Asignatura asignatura) {
        try {
            return ResponseEntity.ok(asignaturaService.actualizarAsignatura(id, asignatura));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe una asignatura con el id " +id);
        }
    }

    @Operation(summary = "Elimina una asignatura", description = "Elimina una asignatura existente por su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Eliminado"),
            @ApiResponse(responseCode = "404", description = "Asignatura no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        try {
            asignaturaService.eliminarAsignatura(id);
            return ResponseEntity.ok("asignatura eliminada con exito");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no existe una asignatura con el id " + id);
        }
    }

    @Operation(summary = "Obtiene una asignatura con sus secciones", description = "Retorna la información de una asignatura junto con sus secciones asociadas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aprobado"),
            @ApiResponse(responseCode = "404", description = "Asignatura no encontrada")
    })
    @GetMapping("/{idAsignatura}/con-secciones")
    public ResponseEntity<AsignaturaSeccionDTO> obtenerConSecciones(
            @PathVariable Integer idAsignatura) {
        return ResponseEntity.ok(asignaturaService.obtenerAsignaturaConSecciones(idAsignatura));
    }
}