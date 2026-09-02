package cl.duoc.asignaturas_service.service;

import cl.duoc.asignaturas_service.model.Asignatura;
import cl.duoc.asignaturas_service.repository.AsignaturaRepository;
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
public class AsignaturaServiceTest {

    @Mock
    private AsignaturaRepository asignaturaRepository;

    @InjectMocks
    private AsignaturaService asignaturaService;

    private Asignatura asignaturaMock;

    @BeforeEach
    void setUp() {
        asignaturaMock = new Asignatura();
        asignaturaMock.setIdAsignatura(1);
        asignaturaMock.setNombreAsignatura("Matemáticas");
        asignaturaMock.setIdProfesor(10);
    }

    @Test
    void testObtenerTodasLasAsignaturas() {
        when(asignaturaRepository.findAll()).thenReturn(Arrays.asList(asignaturaMock));

        List<Asignatura> resultado = asignaturaService.obtenerAsignaturas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Matemáticas", resultado.get(0).getNombreAsignatura());
        verify(asignaturaRepository, times(1)).findAll();
    }

    @Test
    void testGuardarAsignatura() {
        when(asignaturaRepository.save(any(Asignatura.class))).thenReturn(asignaturaMock);

        Asignatura resultado = asignaturaService.guardarAsignatura(asignaturaMock);

        assertNotNull(resultado);
        assertEquals("Matemáticas", resultado.getNombreAsignatura());
        verify(asignaturaRepository, times(1)).save(any(Asignatura.class));
    }
}
