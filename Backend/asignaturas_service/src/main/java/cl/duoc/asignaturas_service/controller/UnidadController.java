package cl.duoc.asignaturas_service.controller;

import cl.duoc.asignaturas_service.dto.AsignaturaDTO;
import cl.duoc.asignaturas_service.dto.UnidadDTO;
import cl.duoc.asignaturas_service.model.Unidad;
import cl.duoc.asignaturas_service.service.UnidadService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.EntityModel;

import java.util.List;
import java.util.Optional;

@Tag(name = "Unidades", description = "Operaciones relacionadas con la gestión de unidades")
@RestController
@RequestMapping("/api/v1/unidades")
public class UnidadController {

    @Autowired
    private UnidadService unidadservice;

    @Operation(summary = "Obtiene todas las unidades", description = "Retorna la lista de todas las unidades")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aprobado"),
            @ApiResponse(responseCode = "204", description = "No hay unidades registradas")
    })
    @GetMapping
    public ResponseEntity<List<Unidad>> obtenerUnidades(){
        List<Unidad> unidades = unidadservice.obtenerUnidades();

        if (unidades.isEmpty())
            return ResponseEntity.noContent().build();
        else
            return ResponseEntity.ok(unidades);
    }

    @Operation(summary = "Obtiene una unidad por su id ", description = "Retorna la informacios de una Unidad específica por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aprobado"),
            @ApiResponse(responseCode = "404", description = "Unidad no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUnidad(@PathVariable Integer id){
        Optional<Unidad> unidadOptional = unidadservice.buscarUnidadPorId(id);

        if (unidadOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No existe una unidad con el ID " + id);
        }

        Unidad unidad = unidadOptional.get();
        EntityModel<Unidad> model = EntityModel.of(unidad);

        model.add(
                linkTo(
                        methodOn(UnidadController.class).obtenerUnidad(id)
                ).withSelfRel()
        );

        model.add(
                Link.of("http://localhost:8084" +
                        "/api/v1/unidades" + id, "eliminar")
        );

        model.add(
                linkTo(
                        methodOn(UnidadController.class)
                                .obtenerUnidades()
                ).withRel("todos-las unidades")
        );
        return ResponseEntity.ok(model);
    }

    @Operation(summary = "Crea una nueva unidad", description = "Registra una unidad nueva en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<Unidad> crearUnidad(@RequestBody @Valid Unidad unidad) {
        return ResponseEntity.ok(unidadservice.guardarUnidad(unidad));
    }

    @Operation(summary = "Actualiza una unidad", description = "Actualiza los datos de una unidad existente por su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aprobado"),
            @ApiResponse(responseCode = "404", description = "Unidad no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable int id, @RequestBody Unidad unidad) {
        try {
            return ResponseEntity.ok(unidadservice.actualizarUnidades(id, unidad));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe una unidad con el id " +id);
        }
    }

    @Operation(summary = "Elimina una unidad", description = "Elimina una unidad existente por su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Eliminado"),
            @ApiResponse(responseCode = "404", description = "Unidad no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        try {
            unidadservice.eliminarUnidad(id);
            return ResponseEntity.ok("unidad eliminada con exito");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no existe una unidad con el id " + id);
        }
    }

}