package cl.duoc.grupos_service.service;

import cl.duoc.grupos_service.client.IntegranteClient;
import cl.duoc.grupos_service.client.TrabajoClient;
import cl.duoc.grupos_service.dto.GrupoIntegranteDTO;
import cl.duoc.grupos_service.dto.GrupoTrabajoDTO;
import cl.duoc.grupos_service.dto.IntegranteDTO;
import cl.duoc.grupos_service.dto.TrabajoDTO;
import cl.duoc.grupos_service.model.Grupo;
import cl.duoc.grupos_service.repository.GrupoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GrupoServiceTest {

    @Mock
    private GrupoRepository grupoRepository;

    @Mock
    private IntegranteClient integranteClient;

    @Mock
    private TrabajoClient trabajoClient;

    @InjectMocks
    private GrupoService grupoService;

    private Grupo grupoMock;

    @BeforeEach
    void setUp() {
        grupoMock = new Grupo();
        grupoMock.setIdGrupo(1);
        grupoMock.setNombreGrupo("Grupo A");
        grupoMock.setCapacidadMaxima(5);
        grupoMock.setFechaCreacion(LocalDate.now());
        grupoMock.setGrupoLleno(false);
    }

    @Test
    void testObtenerGrupos() {
        when(grupoRepository.findAll()).thenReturn(Arrays.asList(grupoMock));

        List<Grupo> resultado = grupoService.obtenerGrupos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Grupo A", resultado.get(0).getNombreGrupo());
    }

    @Test
    void testBuscarGrupoPorId() {
        when(grupoRepository.findById(1)).thenReturn(Optional.of(grupoMock));

        Optional<Grupo> resultado = grupoService.buscarGrupoPorId(1);

        assertTrue(resultado.isPresent());
        assertEquals("Grupo A", resultado.get().getNombreGrupo());
    }

    @Test
    void testGuardarGrupo() {
        when(grupoRepository.save(any(Grupo.class))).thenReturn(grupoMock);

        Grupo resultado = grupoService.guardarGrupo(grupoMock);

        assertNotNull(resultado);
        assertEquals("Grupo A", resultado.getNombreGrupo());
    }

    @Test
    void testObtenerGrupoConIntegrantes() {
        when(grupoRepository.findById(1)).thenReturn(Optional.of(grupoMock));
        IntegranteDTO integranteDTO = new IntegranteDTO();
        integranteDTO.setIdIntegrante(10);
        integranteDTO.setNombre("Pedro");
        when(integranteClient.obtenerPorGrupo(1)).thenReturn(Arrays.asList(integranteDTO));

        GrupoIntegranteDTO resultado = grupoService.obtenerGrupoConIntegrantes(1);

        assertNotNull(resultado);
        assertEquals("Grupo A", resultado.getNombreGrupo());
        assertEquals(1, resultado.getIntegrantes().size());
        assertEquals("Pedro", resultado.getIntegrantes().get(0).getNombre());
    }

    @Test
    void testObtenerGrupoConTrabajo() {
        when(grupoRepository.findById(1)).thenReturn(Optional.of(grupoMock));
        TrabajoDTO trabajoDTO = new TrabajoDTO();
        trabajoDTO.setIdTrabajo(20);
        trabajoDTO.setNombreTrabajo("Proyecto Final");
        when(trabajoClient.obtenerPorGrupo(1)).thenReturn(trabajoDTO);

        GrupoTrabajoDTO resultado = grupoService.obtenerGrupoConTrabajo(1);

        assertNotNull(resultado);
        assertEquals("Grupo A", resultado.getNombreGrupo());
        assertNotNull(resultado.getTrabajo());
        assertEquals("Proyecto Final", resultado.getTrabajo().getNombreTrabajo());
    }

    @Test
    void testObtenerGrupos_CuandoNoExistenGrupos() {
        when(grupoRepository.findAll()).thenReturn(java.util.Collections.emptyList());

        List<Grupo> resultado = grupoService.obtenerGrupos();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void testBuscarGrupoPorId_CuandoNoExiste() {
        when(grupoRepository.findById(99)).thenReturn(Optional.empty());

        Optional<Grupo> resultado = grupoService.buscarGrupoPorId(99);

        assertFalse(resultado.isPresent());
    }

    @Test
    void testObtenerGrupoConIntegrantes_CuandoGrupoNoExiste() {
        when(grupoRepository.findById(99)).thenReturn(Optional.empty());

        // En lugar de assertThrows, ejecutamos el método y capturamos el resultado
        cl.duoc.grupos_service.dto.GrupoIntegranteDTO resultado = grupoService.obtenerGrupoConIntegrantes(99);

        // Verificamos que el código maneje la ausencia del grupo devolviendo null
        assertNull(resultado);
        
        // Seguimos verificando que no se llame al otro microservicio
        verify(integranteClient, never()).obtenerPorGrupo(anyInt());
    }
}
