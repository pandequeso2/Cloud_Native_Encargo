package cl.duoc.roles_service.service;

import cl.duoc.roles_service.model.Rol;
import cl.duoc.roles_service.repository.RolRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RolServiceTest {

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private RolService rolService;

    private Rol rolMock;

    @BeforeEach
    void setUp() {
        rolMock = new Rol();
        rolMock.setIdRol(1);
        rolMock.setRol("ESTUDIANTE");
    }

    @Test
    void testObtenerTodosLosRoles() {
        when(rolRepository.findAll()).thenReturn(Arrays.asList(rolMock));

        List<Rol> resultado = rolService.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("ESTUDIANTE", resultado.get(0).getRol());
        verify(rolRepository, times(1)).findAll();
    }

    @Test
    void testGuardarRol() {
        when(rolRepository.save(any(Rol.class))).thenReturn(rolMock);

        Rol resultado = rolService.guardar(rolMock);

        assertNotNull(resultado);
        assertEquals("ESTUDIANTE", resultado.getRol());
        verify(rolRepository, times(1)).save(any(Rol.class));
    }
}
