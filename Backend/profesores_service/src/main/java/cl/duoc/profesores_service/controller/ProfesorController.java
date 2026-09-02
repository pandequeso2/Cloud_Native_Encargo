package cl.duoc.profesores_service.controller;

import cl.duoc.profesores_service.dto.ProfesorSeccionDTO;
import cl.duoc.profesores_service.model.Profesor;
import cl.duoc.profesores_service.service.ProfesorService;
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

@Tag(name = "Profesores", description = "Operaciones relacionadas con la gestión de profesores")
@RestController
@RequestMapping("/api/v1/profesores")
public class ProfesorController {

    @Autowired
    private ProfesorService profesorService;

    @Operation(summary = "Obtiene todos los profesores", description = "Retorna la lista de todos los profesores")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aprobado"),
            @ApiResponse(responseCode = "404", description = "No se encontraron profesores")
    })
    @GetMapping
    public ResponseEntity<?> obtenerProfesores() {
        List<Profesor> profesores = profesorService.obtenerProfesores();
        if (profesores.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay profesores");
        }
        return ResponseEntity.ok(profesores);
    }

    @Operation(summary = "Obtiene un profesor por su id ", description = "Retorna la informacios de un profesor específico por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aprobado"),
            @ApiResponse(responseCode = "404", description = "Profesor no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerProfesor(@PathVariable Integer id){
        Optional<Profesor> profesorOptional = profesorService.buscarProfesorPorId(id);

        if (profesorOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No existe un profesor con el ID " + id);
        }

        Profesor profesor = profesorOptional.get();
        EntityModel<Profesor> model = EntityModel.of(profesor);

        model.add(
                linkTo(
                        methodOn(ProfesorController.class).obtenerProfesor(id)
                ).withSelfRel()
        );

        model.add(
                Link.of("http://localhost:8085" +
                        "/api/v1/profesores" + id, "eliminar")
        );

        model.add(
                linkTo(
                        methodOn(ProfesorController.class)
                                .obtenerProfesores()
                ).withRel("todos-los profesores")
        );
        return ResponseEntity.ok(model);
    }

    @Operation(summary = "Crea un nuevo profesor", description = "Registra un profesor nuevo en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<Profesor> crearProfesor(@RequestBody @Valid Profesor profesor) {
        return ResponseEntity.status(HttpStatus.CREATED).body(profesorService.guardarProfesor(profesor));
    }

    @Operation(summary = "Actualiza un profesor", description = "Actualiza los datos de un profesor existente por su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aprobado"),
            @ApiResponse(responseCode = "404", description = "Profesor no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProfesor(@PathVariable int id, @RequestBody Profesor profesor) {
        try {
            return ResponseEntity.ok(profesorService.actualizarProfesor(id, profesor));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe profesor con el id " +id);
        }
    }

    @Operation(summary = "Elimina un profesor", description = "Elimina un profesor existente por su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Eliminado"),
            @ApiResponse(responseCode = "404", description = "Profesor no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarProfesor(@PathVariable Integer id) {
        try {
            profesorService.eliminarProfesor(id);
            return ResponseEntity.ok("profesor eliminado con exito");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no existe un profesor con el id " + id);
        }
    }

    @Operation(summary = "Obtiene un profesor con sus secciones", description = "Retorna la información de un profesor junto con sus secciones asociadas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aprobado"),
            @ApiResponse(responseCode = "404", description = "Profesor no encontrado")
    })
    @GetMapping("/{idProfesor}/con-secciones")
    public ResponseEntity<ProfesorSeccionDTO> obtenerConSecciones(
            @PathVariable Integer idProfesor) {
        return ResponseEntity.ok(profesorService.obtenerProfesorConSecciones(idProfesor));
    }
}