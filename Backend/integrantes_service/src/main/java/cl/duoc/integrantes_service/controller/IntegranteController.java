package cl.duoc.integrantes_service.controller;

import cl.duoc.integrantes_service.dto.IntegranteRolDTO;
import cl.duoc.integrantes_service.model.Integrante;
import cl.duoc.integrantes_service.service.IntegranteService;
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

@Tag(name = "Integrantes", description = "Operaciones relacionadas con la gestión de integrantes")
@RestController
@RequestMapping("api/v1/integrantes")
public class IntegranteController {

    @Autowired
    private IntegranteService integranteService;

    @Operation(summary = "Obtiene todos los integrantes", description = "Retorna la lista de todos los integrantes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aprobado"),
            @ApiResponse(responseCode = "404", description = "No se encontraron integrantes")
    })
    @GetMapping
    public ResponseEntity<?> obtenerIntegrantes() {
        List<Integrante> integrantes = integranteService.obtenerIntegrantes();
        if (integrantes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay integrantes");
        }
        return ResponseEntity.ok(integrantes);
    }

    @Operation(summary = "Obtiene un integrante por su id ", description = "Retorna la informacios de un Integrante específico por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aprobado"),
            @ApiResponse(responseCode = "404", description = "Integrante no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerIntegrante(@PathVariable Integer id){
        Optional<Integrante> integranteOptional = integranteService.buscarIntegrantePorId(id);

        if (integranteOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No existe un integrante con el ID " + id);
        }

        Integrante integrante = integranteOptional.get();
        EntityModel<Integrante> model = EntityModel.of(integrante);

        model.add(
                linkTo(
                        methodOn(IntegranteController.class).obtenerIntegrante(id)
                ).withSelfRel()
        );

        model.add(
                Link.of("http://localhost:8087" +
                        "/api/v1/integrantes" + id, "eliminar")
        );

        model.add(
                linkTo(
                        methodOn(IntegranteController.class)
                                .obtenerIntegrantes()
                ).withRel("todos-los integrantes")
        );
        return ResponseEntity.ok(model);
    }

    @Operation(summary = "Crea un nuevo integrante", description = "Registra un integrante nuevo en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<?> crearIntegrante(@Valid @RequestBody Integrante integrante) {
        return ResponseEntity.status(HttpStatus.CREATED).body(integranteService.guardarIntegrante(integrante));
    }

    @Operation(summary = "Actualiza un integrante", description = "Actualiza los datos de un integrante existente por su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aprobado"),
            @ApiResponse(responseCode = "404", description = "Integrante no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable int id, @RequestBody Integrante integrante) {
        try {
            return ResponseEntity.ok(integranteService.actualizarIntegrante(id, integrante));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe un integrante con el id " +id);
        }
    }

    @Operation(summary = "Elimina un integrante", description = "Elimina un integrante existente por su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Eliminado"),
            @ApiResponse(responseCode = "404", description = "Integrante no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        try {
            integranteService.eliminarIntegrante(id);
            return ResponseEntity.ok("integrante eliminado con exito");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no existe un integrante con el id " + id);
        }
    }

    @Operation(summary = "Obtiene integrantes por grupo", description = "Retorna la lista de integrantes pertenecientes a un grupo específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aprobado"),
            @ApiResponse(responseCode = "404", description = "Grupo no encontrado")
    })
    @GetMapping("/grupo/{idGrupo}")
    public ResponseEntity<List<Integrante>> obtenerPorGrupo(@PathVariable Integer idGrupo) {
        return ResponseEntity.ok(integranteService.obtenerPorGrupo(idGrupo));
    }

    @Operation(summary = "Obtiene un integrante con su rol", description = "Retorna la información de un integrante junto a los datos de su rol")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aprobado"),
            @ApiResponse(responseCode = "404", description = "Integrante no encontrado")
    })
    @GetMapping("/{idIntegrante}/con-rol")
    public ResponseEntity<IntegranteRolDTO> obtenerConRol(
            @PathVariable Integer idIntegrante) {
        return ResponseEntity.ok(integranteService.obtenerIntegranteConRol(idIntegrante));
    }
}