package cl.duoc.notas_service.controller;

import cl.duoc.notas_service.dto.NotasIntegranteDTO;
import cl.duoc.notas_service.model.Nota;
import cl.duoc.notas_service.service.NotaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.hateoas.Link;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.EntityModel;

import java.util.List;
import java.util.Optional;
@Tag(name = "Notas", description = "Operaciones relacionadas con la gestión de notas")
@RestController
@RequestMapping("/api/v1/notas")
public class NotaController {

    @Autowired
    private NotaService notaService;

    @Operation(summary = "Obtiene todas las notas", description = "Retorna la lista de todas las notas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aprobado"),
            @ApiResponse(responseCode = "404", description = "No se encontraron notas")
    })
    @GetMapping
    public ResponseEntity<?> obtenerNotas() {
        List<Nota> notas = notaService.obtenerNotas();
        if (notas.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay notas");
        }
        return ResponseEntity.ok(notas);
    }

    @Operation(summary = "Obtiene una nota por su id ", description = "Retorna la informacios de una nota específica por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aprobado"),
            @ApiResponse(responseCode = "404", description = "Nota no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerNota(@PathVariable Integer id){
        Optional<Nota> notaOptional = notaService.buscarNotaPorId(id);

        if (notaOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No existe una nota con el ID " + id);
        }

        Nota nota = notaOptional.get();
        EntityModel<Nota> model = EntityModel.of(nota);

        model.add(
                linkTo(
                        methodOn(NotaController.class).obtenerNota(id)
                ).withSelfRel()
        );

        model.add(
                Link.of("http://localhost:8083" +
                        "/api/v1/notas" + id, "eliminar")
        );

        model.add(
                linkTo(
                        methodOn(NotaController.class)
                                .obtenerNotas()
                ).withRel("todos-los integrantes")
        );
        return ResponseEntity.ok(model);
    }
    @Operation(summary = "Crea una nueva nota", description = "Registra una nota nueva en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<Nota> crearNota(@RequestBody @Valid Nota nota) {
        return ResponseEntity.ok(notaService.guardarNota(nota));
    }

    @Operation(summary = "Actualiza una nota", description = "Actualiza los datos de una nota existente por su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aprobado"),
            @ApiResponse(responseCode = "404", description = "Nota no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable int id, @RequestBody Nota nota) {
        try {
            return ResponseEntity.ok(notaService.actualizarNota(id, nota));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe una nota con el id " +id);
        }
    }

    @Operation(summary = "Elimina una nota", description = "Elimina una nota existente por su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Eliminado"),
            @ApiResponse(responseCode = "404", description = "Nota no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        try {
            notaService.eliminarNota(id);
            return ResponseEntity.ok("nota eliminada con exito");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no existe una nota con el id " + id);
        }
    }

    @Operation(summary = "Obtiene las notas de un integrante", description = "Retorna las notas asociadas a un integrante específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aprobado"),
            @ApiResponse(responseCode = "404", description = "Integrante no encontrado")
    })
    @GetMapping("{idIntegrante}/con-integrante")
    public ResponseEntity<NotasIntegranteDTO> getNotasConIntegrante(@PathVariable Integer idIntegrante) {
        NotasIntegranteDTO dto = notaService.obtenerNotasConIntegrante(idIntegrante);

        if (dto == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(dto);
    }
}