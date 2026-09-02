package cl.duoc.asignaturas_service.service;

import cl.duoc.asignaturas_service.model.Unidad;
import cl.duoc.asignaturas_service.repository.UnidadRepository;
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
public class UnidadServiceTest {

    @Mock
    private UnidadRepository unidadRepository;

    @InjectMocks
    private UnidadService unidadService;

    private Unidad unidadMock;

    @BeforeEach
    void setUp() {
        unidadMock = new Unidad();
        unidadMock.setIdUnidad(1);
        unidadMock.setIdAsignatura(1);
        unidadMock.setNumeroUnidad(1);
        unidadMock.setNombreUnidad("Usar print");
    }

    @Test
    void testObtenerTodasLasUnidades() {
        when(unidadRepository.findAll()).thenReturn(Arrays.asList(unidadMock));

        List<Unidad> resultado = unidadService.obtenerUnidades();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Usar print", resultado.get(0).getNombreUnidad());
        verify(unidadRepository, times(1)).findAll();
    }

    @Test
    void testGuardarUnidad() {
        when(unidadRepository.save(any(Unidad.class))).thenReturn(unidadMock);

        Unidad resultado = unidadService.guardarUnidad(unidadMock);

        assertNotNull(resultado);
        assertEquals("Usar print", resultado.getNombreUnidad());
        verify(unidadRepository, times(1)).save(any(Unidad.class));
    }

    @Test
    void testBuscarUnidadPorId_existente() {
        when(unidadRepository.findById(1)).thenReturn(Optional.of(unidadMock));

        Optional<Unidad> resultado = unidadService.buscarUnidadPorId(1);

        assertTrue(resultado.isPresent());
        assertEquals("Usar print", resultado.get().getNombreUnidad());
        verify(unidadRepository, times(1)).findById(1);
    }

    @Test
    void testBuscarUnidadPorId_noExistente() {
        when(unidadRepository.findById(99)).thenReturn(Optional.empty());

        Optional<Unidad> resultado = unidadService.buscarUnidadPorId(99);

        assertFalse(resultado.isPresent());
        verify(unidadRepository, times(1)).findById(99);
    }

    @Test
    void testActualizarUnidades_existente() {
        when(unidadRepository.existsById(1)).thenReturn(true);
        when(unidadRepository.save(any(Unidad.class))).thenReturn(unidadMock);

        Unidad resultado = unidadService.actualizarUnidades(1, unidadMock);

        assertNotNull(resultado);
        assertEquals(1, resultado.getIdUnidad());
        verify(unidadRepository, times(1)).existsById(1);
        verify(unidadRepository, times(1)).save(any(Unidad.class));
    }

    @Test
    void testActualizarUnidades_noExistente_lanzaExcepcion() {
        when(unidadRepository.existsById(99)).thenReturn(false);

        RuntimeException excepcion = assertThrows(RuntimeException.class,
                () -> unidadService.actualizarUnidades(99, unidadMock));

        assertTrue(excepcion.getMessage().contains("99"));
        verify(unidadRepository, never()).save(any(Unidad.class));
    }

    @Test
    void testEliminarUnidad_existente() {
        when(unidadRepository.existsById(1)).thenReturn(true);

        unidadService.eliminarUnidad(1);

        verify(unidadRepository, times(1)).deleteById(1);
    }

    @Test
    void testEliminarUnidad_noExistente_lanzaExcepcion() {
        when(unidadRepository.existsById(99)).thenReturn(false);

        RuntimeException excepcion = assertThrows(RuntimeException.class,
                () -> unidadService.eliminarUnidad(99));

        assertTrue(excepcion.getMessage().contains("99"));
        verify(unidadRepository, never()).deleteById(anyInt());
    }
}