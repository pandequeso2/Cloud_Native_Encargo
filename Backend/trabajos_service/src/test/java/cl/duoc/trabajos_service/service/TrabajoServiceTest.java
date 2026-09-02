package cl.duoc.trabajos_service.service;

import cl.duoc.trabajos_service.model.Trabajo;
import cl.duoc.trabajos_service.repository.TrabajoRepository;
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
public class TrabajoServiceTest {

    @Mock
    private TrabajoRepository repository;

    @InjectMocks
    private TrabajoService service;

    private Trabajo trabajoMock;

    @BeforeEach
    void setUp() {
        trabajoMock = new Trabajo();
        trabajoMock.setIdTrabajo(1);
        trabajoMock.setNombreTrabajo("Avance 1");
        trabajoMock.setPorcentajeNota(0.2);
        trabajoMock.setIdGrupo(2);
        trabajoMock.setTipoTrabajo(Trabajo.TipoTrabajo.ENCARGO);
        trabajoMock.setSemestre(1);
        trabajoMock.setEstado(Trabajo.Estado.PENDIENTE);
    }

    @Test
    void testObtenerTrabajos() {
        when(repository.findAll()).thenReturn(Arrays.asList(trabajoMock));

        List<Trabajo> resultado = service.obtenerTrabajos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Avance 1", resultado.get(0).getNombreTrabajo());
    }

    @Test
    void testBuscarTrabajoPorId() {
        when(repository.findById(1)).thenReturn(Optional.of(trabajoMock));

        Optional<Trabajo> resultado = service.buscarTrabajoPorId(1);

        assertTrue(resultado.isPresent());
        assertEquals(0.2, resultado.get().getPorcentajeNota());
    }

    @Test
    void testGuardarTrabajo() {
        when(repository.save(any(Trabajo.class))).thenReturn(trabajoMock);

        Trabajo resultado = service.guardarTrabajo(trabajoMock);

        assertNotNull(resultado);
        assertEquals("Avance 1", resultado.getNombreTrabajo());
    }

    @Test
    void testObtenerPorGrupo() {
        when(repository.findByIdGrupo(2)).thenReturn(trabajoMock);

        Trabajo resultado = service.obtenerPorGrupo(2);

        assertNotNull(resultado);
        assertEquals("Avance 1", resultado.getNombreTrabajo());
    }
}
