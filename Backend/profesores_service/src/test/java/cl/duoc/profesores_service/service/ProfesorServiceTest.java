package cl.duoc.profesores_service.service;

import cl.duoc.profesores_service.client.SeccionClient;
import cl.duoc.profesores_service.dto.ProfesorSeccionDTO;
import cl.duoc.profesores_service.dto.SeccionDTO;
import cl.duoc.profesores_service.model.Profesor;
import cl.duoc.profesores_service.repository.ProfesorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProfesorServiceTest {

    @Mock
    private ProfesorRepository repository;

    @Mock
    private SeccionClient seccionClient;

    @InjectMocks
    private ProfesorService service;

    private Profesor profesorMock;

    @BeforeEach
    void setUp() {
        profesorMock = new Profesor();
        profesorMock.setIdProfesor(1);
        profesorMock.setNombreProfesor("Andrés");
        profesorMock.setApellidoProfesor("Bello");
        profesorMock.setRutCuerpo(11222333);
        profesorMock.setRutDv("4");
        profesorMock.setCorreoElectronico("andres.bello@example.com");
    }

    @Test
    void testObtenerProfesores() {
        when(repository.findAll()).thenReturn(Arrays.asList(profesorMock));

        List<Profesor> resultado = service.obtenerProfesores();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Andrés", resultado.get(0).getNombreProfesor());
    }

    @Test
    void testBuscarProfesorPorId() {
        when(repository.findById(1)).thenReturn(Optional.of(profesorMock));

        Optional<Profesor> resultado = service.buscarProfesorPorId(1);

        assertTrue(resultado.isPresent());
        assertEquals("Bello", resultado.get().getApellidoProfesor());
    }

    @Test
    void testGuardarProfesor() {
        when(repository.save(any(Profesor.class))).thenReturn(profesorMock);

        Profesor resultado = service.guardarProfesor(profesorMock);

        assertNotNull(resultado);
        assertEquals("Andrés", resultado.getNombreProfesor());
    }

    @Test
    void testObtenerProfesorConSecciones() {
        when(repository.findById(1)).thenReturn(Optional.of(profesorMock));
        SeccionDTO seccionDTO = new SeccionDTO();
        seccionDTO.setIdSeccion(5);
        seccionDTO.setCodigoSeccion("INF-101");
        when(seccionClient.obtenerPorProfesor(1)).thenReturn(Arrays.asList(seccionDTO));

        ProfesorSeccionDTO resultado = service.obtenerProfesorConSecciones(1);

        assertNotNull(resultado);
        assertEquals("Andrés", resultado.getNombre());
        assertEquals(1, resultado.getSecciones().size());
        assertEquals("INF-101", resultado.getSecciones().get(0).getCodigoSeccion());
    }
}
