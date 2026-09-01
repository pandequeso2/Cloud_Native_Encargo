package cl.duoc.roles_service.controller;

import cl.duoc.roles_service.model.Rol;
import cl.duoc.roles_service.service.RolService;
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

@Tag(name = "Roles", description = "Operaciones relacionadas con la gestión de roles")
@RestController
@RequestMapping("api/v1/roles")
public class RolController {

    @Autowired
    private RolService rolService;

    @Operation(summary = "Obtiene todos los roles", description = "Retorna la lista de todos los roles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aprobado"),
            @ApiResponse(responseCode = "404", description = "No se encontraron roles")
    })
    @GetMapping
    public ResponseEntity<?> obtenerRoles() {
        List<Rol> roles = rolService.listarTodos();
        if (roles.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No hay roles");
        }
        return ResponseEntity.ok(roles);
    }

    @Operation(summary = "Obtiene un rol por su id ", description = "Retorna la informacion de un rol específico por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aprobado"),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerRol(@PathVariable Integer id){
        Optional<Rol> rolOptional = rolService.buscarRolPorId(id);

        if (rolOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No existe un rol con el ID " + id);
        }

        Rol rol = rolOptional.get();
        EntityModel<Rol> model = EntityModel.of(rol);

        model.add(
                linkTo(
                        methodOn(RolController.class).obtenerRol(id)
                ).withSelfRel()
        );

        model.add(
                Link.of("http://localhost:8088" +
                        "/api/v1/roles" + id, "eliminar")
        );

        model.add(
                linkTo(
                        methodOn(RolController.class)
                                .obtenerRoles()
                ).withRel("todos-los roles")
        );
        return ResponseEntity.ok(model);
    }

    @Operation(summary = "Crea un nuevo rol", description = "Registra un rol nuevo en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<Rol> crearRol(@RequestBody @Valid Rol rol) {
        return ResponseEntity.ok(rolService.guardar(rol));
    }

    @Operation(summary = "Actualiza un rol", description = "Actualiza los datos de un rol existente por su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aprobado"),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarRol(@PathVariable int id, @RequestBody Rol rol) {
        try {
            return ResponseEntity.ok(rolService.actualizar(id, rol));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe rol con el id " +id);
        }
    }

    @Operation(summary = "Elimina un rol", description = "Elimina un rol existente por su id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Eliminado"),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarRol(@PathVariable Integer id) {
        try {
            rolService.eliminarRol(id);
            return ResponseEntity.ok("Rol eliminado con exito");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no existe un rol con el id " + id);
        }
    }
}