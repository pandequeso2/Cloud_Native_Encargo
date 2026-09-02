package cl.duoc.secciones_service.service;

import cl.duoc.secciones_service.client.IntegranteClient;
import cl.duoc.secciones_service.dto.IntegranteDTO;
import cl.duoc.secciones_service.dto.SeccionIntegranteDTO;
import cl.duoc.secciones_service.model.Seccion;
import cl.duoc.secciones_service.repository.SeccionRepository;
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
public class SeccionServiceTest {

    @Mock
    private SeccionRepository repository;

    @Mock
    private IntegranteClient integranteClient;

    @InjectMocks
    private SeccionService service;

    private Seccion seccionMock;

    @BeforeEach
    void setUp() {
        seccionMock = new Seccion();
        seccionMock.setIdSeccion(1);
        seccionMock.setCodigoSeccion("INF-202");
        seccionMock.setCapacidadMaxima(30);
        seccionMock.setIdAsignatura(2);
        seccionMock.setIdProfesor(3);
    }

    @Test
    void testObtenerSecciones() {
        when(repository.findAll()).thenReturn(Arrays.asList(seccionMock));

        List<Seccion> resultado = service.obtenerSecciones();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("INF-202", resultado.get(0).getCodigoSeccion());
    }

    @Test
    void testBuscarSeccionPorId() {
        when(repository.findById(1)).thenReturn(Optional.of(seccionMock));

        Optional<Seccion> resultado = service.buscarSeccionPorId(1);

        assertTrue(resultado.isPresent());
        assertEquals(30, resultado.get().getCapacidadMaxima());
    }

    @Test
    void testGuardarSeccion() {
        when(repository.save(any(Seccion.class))).thenReturn(seccionMock);

        Seccion resultado = service.guardarSeccion(seccionMock);

        assertNotNull(resultado);
        assertEquals("INF-202", resultado.getCodigoSeccion());
    }

    @Test
    void testObtenerSeccionConIntegrante() {
        when(repository.findById(1)).thenReturn(Optional.of(seccionMock));
        IntegranteDTO integranteDTO = new IntegranteDTO();
        integranteDTO.setIdIntegrante(10);
        integranteDTO.setNombre("Pedro");
        when(integranteClient.obtenerIntegrante(10)).thenReturn(integranteDTO);

        SeccionIntegranteDTO resultado = service.obtenerSeccionConIntegrante(1, 10);

        assertNotNull(resultado);
        assertEquals("INF-202", resultado.getCodigoSeccion());
        assertNotNull(resultado.getIntegrante());
        assertEquals("Pedro", resultado.getIntegrante().getNombre());
    }
}
