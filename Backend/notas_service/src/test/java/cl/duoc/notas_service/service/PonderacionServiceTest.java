package cl.duoc.notas_service.service;

import cl.duoc.notas_service.model.Ponderacion;
import cl.duoc.notas_service.repository.PonderacionRepository;
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
public class PonderacionServiceTest {

    @Mock
    private PonderacionRepository repository;

    @InjectMocks
    private PonderacionService service;

    private Ponderacion ponderacionMock;

    @BeforeEach
    void setUp() {
        ponderacionMock = new Ponderacion();
        ponderacionMock.setIdPonderacion(1);
        ponderacionMock.setPorcentaje(0.25);
    }

    @Test
    void testObtenerPonderaciones() {
        when(repository.findAll()).thenReturn(Arrays.asList(ponderacionMock));

        List<Ponderacion> resultado = service.obtenerPonderacion();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(0.25, resultado.get(0).getPorcentaje());
    }

    @Test
    void testBuscarPorId() {
        when(repository.findById(1)).thenReturn(Optional.of(ponderacionMock));

        Optional<Ponderacion> resultado = service.buscarPonderacionPorId(1);

        assertTrue(resultado.isPresent());
        assertEquals(0.25, resultado.get().getPorcentaje());
    }

    @Test
    void testGuardar() {
        when(repository.save(any(Ponderacion.class))).thenReturn(ponderacionMock);

        Ponderacion resultado = service.guardarPonderacion(ponderacionMock);

        assertNotNull(resultado);
        assertEquals(0.25, resultado.getPorcentaje());
    }
}
