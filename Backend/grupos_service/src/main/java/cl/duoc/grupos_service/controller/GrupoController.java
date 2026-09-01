package cl.duoc.grupos_service.controller;

import cl.duoc.grupos_service.dto.GrupoIntegranteDTO;
import cl.duoc.grupos_service.dto.GrupoTrabajoDTO;
import cl.duoc.grupos_service.model.Grupo;
import cl.duoc.grupos_service.service.GrupoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Grupos", description = "Operaciones relacionadas con la gestión de grupos")
@RestController
@RequestMapping("api/v1/grupos")
public class GrupoController {

    @Autowired
    private GrupoService grupoService;

    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aprobado"),
            @ApiResponse(responseCode = "404", description = "No se encontraron grupos")
    })
    public ResponseEntity<?> obtenerGrupos() {
        List<Grupo> grupos = grupoService.obtenerGrupos();
        if (grupos.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay grupo");
        }
        return ResponseEntity.ok(grupos);
    }

    @Operation(summary = "Obtiene un grupo por su id ", description = "Retorna la informacios de un grupo específico por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aprobado"),
            @ApiResponse(responseCode = "404", description = "Grupo no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerGrupo(@PathVariable Integer id){
        Optional<Grupo> grupoOptional = grupoService.buscarGrupoPorId(id);

        if (grupoOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No existe un grupo con el ID " + id);
        }

        Grupo grupo = grupoOptional.get();
        EntityModel<Grupo> model = EntityModel.of(grupo);

        model.add(
                linkTo(
                        methodOn(GrupoController.class).obtenerGrupo(id)
                ).withSelfRel()
        );

        model.add(
                Link.of("http://localhost:8086" +
                        "/api/v1/grupos" + id, "eliminar")
        );

        model.add(
                linkTo(
                        methodOn(GrupoController.class)
                                .obtenerGrupos()
                ).withRel("todos-los grupos")
        );
        return ResponseEntity.ok(model);
    }

    /*@GetMapping("/{id}")
    public ResponseEntity<?> obtenerGrupoPorId(@PathVariable Integer id) {
        Optional<Grupo> grupo = grupoService.obtenerGrupoPorId(id);
        if (grupo.isPresent()) {
            return ResponseEntity.ok(grupo.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe grupo con el ID "+ id);
    }*/

    @PostMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<Grupo> crearGrupo(@RequestBody @Valid Grupo grupo) {
        return ResponseEntity.ok(grupoService.guardarGrupo(grupo));
    }

    @PutMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aprobado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "No encontrado")
    })
    public ResponseEntity<?> actualizar(@PathVariable int id, @RequestBody Grupo grupo) {
        try {
            return ResponseEntity.ok(grupoService.actualizarGrupo(id, grupo));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe grupo con el id " +id);
        }
    }

    @DeleteMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Eliminado"),
            @ApiResponse(responseCode = "404", description = "No encontrado")
    })
    public ResponseEntity<String> eliminar(@PathVariable int id) {
        try {
            grupoService.eliminarGrupo(id);
            return ResponseEntity.ok("grupo eliminado con exito");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no existe un grupo con el id " + id);
        }
    }

    @GetMapping("/{idGrupo}/con-integrantes")
    
    public ResponseEntity<GrupoIntegranteDTO> getGrupoConIntegrantes(
            @PathVariable Integer idGrupo) {
        GrupoIntegranteDTO dto = grupoService.obtenerGrupoConIntegrantes(idGrupo);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{idGrupo}/con-trabajo")
    public ResponseEntity<GrupoTrabajoDTO> obtenerConTrabajo(
            @PathVariable Integer idGrupo) {
        return ResponseEntity.ok(grupoService.obtenerGrupoConTrabajo(idGrupo));
    }
}