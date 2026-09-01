package cl.duoc.secciones_service.controller;

import cl.duoc.secciones_service.dto.SeccionIntegranteDTO;
import cl.duoc.secciones_service.model.Seccion;
import cl.duoc.secciones_service.service.SeccionService;
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

@Tag(name = "Secciones", description = "Operaciones relacionadas con la gestión de secciones")
@RestController
@RequestMapping("/api/v1/secciones")
public class SeccionController {

    @Autowired
    private SeccionService seccionService;

    @Operation(summary = "Obtiene todas las secciones", description = "Retorna la lista de todas las secciones")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aprobado"),
            @ApiResponse(responseCode = "204", description = "No hay secciones registradas")
    })
    @GetMapping
    public ResponseEntity<List<Seccion>> obtenerSecciones(){
        List<Seccion> secciones = seccionService.obtenerSecciones();

        if (secciones.isEmpty())
            return ResponseEntity.noContent().build();
        else
            return ResponseEntity.ok(secciones);
    }

    @Operation(summary = "Obtiene una seccion por su id ", description = "Retorna la informacios de una seccion específico por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aprobado"),
            @ApiResponse(responseCode = "404", description = "Seccion no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerSeccion(@PathVariable Integer id){
        Optional<Seccion> seccionOptional = seccionService.buscarSeccionPorId(id);

        if (seccionOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No existe una seccion con el ID " + id);
        }

        Seccion seccion = seccionOptional.get();
        EntityModel<Seccion> model = EntityModel.of(seccion);

        model.add(
                linkTo(
                        methodOn(SeccionController.class).obtenerSeccion(id)
                ).withSelfRel()
        );

        model.add(
                Link.of("http://localhost:8089" +
                        "/api/v1/secciones" + id, "eliminar")
        );

        model.add(
                linkTo(
                        methodOn(SeccionController.class)
                                .obtenerSecciones()
                ).withRel("todos-los secciones")
        );
        return ResponseEntity.ok(model);
    }

    @Operation(summary = "Crea una nueva seccion", description = "Registra una seccion nueva en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<Seccion> crearSeccion(@RequestBody @Valid Seccion seccion) {
        return ResponseEntity.ok(seccionService.guardarSeccion(seccion));
    }

    @Operation(summary = "Actualiza una seccion", description = "Actualiza los datos de una seccion existente por su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aprobado"),
            @ApiResponse(responseCode = "404", description = "Seccion no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable int id, @RequestBody Seccion seccion) {
        try {
            return ResponseEntity.ok(seccionService.actualizarSeccion(id, seccion));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe seccion con el id " +id);
        }
    }

    @Operation(summary = "Elimina una seccion", description = "Elimina una seccion existente por su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Eliminado"),
            @ApiResponse(responseCode = "404", description = "Seccion no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarSeccion(@PathVariable Integer id) {
        try {
            seccionService.eliminarSeccion(id);
            return ResponseEntity.ok("Seccion eliminada con exito");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no existe seccion con el id "+id);
        }
    }

    @Operation(summary = "Obtiene una seccion con un integrante", description = "Retorna la información de una seccion junto con un integrante específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aprobado"),
            @ApiResponse(responseCode = "404", description = "Seccion o integrante no encontrado")
    })
    @GetMapping("/{idSeccion}/con-integrante/{idIntegrante}")
    public ResponseEntity<SeccionIntegranteDTO> getSeccionConIntegrante(
            @PathVariable Integer idSeccion, @PathVariable Integer idIntegrante){

        SeccionIntegranteDTO dto = seccionService.obtenerSeccionConIntegrante(idSeccion, idIntegrante);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Obtiene secciones por asignatura", description = "Retorna la lista de secciones pertenecientes a una asignatura específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aprobado"),
            @ApiResponse(responseCode = "404", description = "Asignatura no encontrada")
    })
    @GetMapping("/asignatura/{idAsignatura}")
    public ResponseEntity<List<Seccion>> obtenerPorAsignatura(
            @PathVariable Integer idAsignatura) {
        return ResponseEntity.ok(seccionService.obtenerPorAsignatura(idAsignatura));
    }

    @Operation(summary = "Obtiene secciones por profesor", description = "Retorna la lista de secciones asignadas a un profesor específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aprobado"),
            @ApiResponse(responseCode = "404", description = "Profesor no encontrado")
    })
    @GetMapping("/profesor/{idProfesor}")
    public ResponseEntity<List<Seccion>> obtenerPorProfesor(@PathVariable Integer idProfesor) {
        return ResponseEntity.ok(seccionService.obtenerPorProfesor(idProfesor));
    }

}