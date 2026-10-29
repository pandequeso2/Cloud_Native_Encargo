package cl.duoc.notas_service.service;

import cl.duoc.notas_service.client.IntegranteClient;
import cl.duoc.notas_service.dto.IntegranteDTO;
import cl.duoc.notas_service.dto.NotasIntegranteDTO;
import cl.duoc.notas_service.model.Nota;
import cl.duoc.notas_service.model.Ponderacion;
import cl.duoc.notas_service.repository.NotaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotaServiceTest {

    @Mock
    private NotaRepository notaRepository;

    @Mock
    private IntegranteClient integranteClient;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private NotaService notaService;

    private Nota notaMock;
    private Ponderacion ponderacionMock;

    @BeforeEach
    void setUp() {
        ponderacionMock = new Ponderacion();
        ponderacionMock.setIdPonderacion(1);
        ponderacionMock.setPorcentaje(0.3);

        notaMock = new Nota();
        notaMock.setIdNota(1);
        notaMock.setNota(6.5);
        notaMock.setPonderacion(ponderacionMock);
        notaMock.setIdIntegrante(2);
        notaMock.setAsignatura("Arquitectura de Software");
    }

    @Test
    void testObtenerNotas() {
        when(notaRepository.findAll()).thenReturn(Arrays.asList(notaMock));

        List<Nota> resultado = notaService.obtenerNotas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(6.5, resultado.get(0).getNota());
    }

    @Test
    void testBuscarNotaPorId() {
        when(notaRepository.findById(1)).thenReturn(Optional.of(notaMock));

        Optional<Nota> resultado = notaService.buscarNotaPorId(1);

        assertTrue(resultado.isPresent());
        assertEquals(2, resultado.get().getIdIntegrante());
    }

    @Test
    void testGuardarNota() {
        when(notaRepository.save(any(Nota.class))).thenReturn(notaMock);

        Nota resultado = notaService.guardarNota(notaMock);

        assertNotNull(resultado);
        assertEquals(6.5, resultado.getNota());
        // Verifica que se haya enviado el evento a Kafka con la clave correspondiente al estudiante
        verify(kafkaTemplate, times(1)).send(eq("academic-grades-input"), eq("2"), anyString());
    }

    @Test
    void testObtenerNotasConIntegrante() {
        when(notaRepository.findByIdIntegrante(2)).thenReturn(Arrays.asList(notaMock));
        IntegranteDTO integranteDTO = new IntegranteDTO();
        integranteDTO.setIdIntegrante(2);
        integranteDTO.setNombre("Carlos");
        when(integranteClient.obtenerIntegrante(2)).thenReturn(integranteDTO);

        NotasIntegranteDTO resultado = notaService.obtenerNotasConIntegrante(2);

        assertNotNull(resultado);
        assertEquals("Carlos", resultado.getIntegrante().getNombre());
        assertEquals(1, resultado.getNotas().size());
        assertEquals(6.5, resultado.getNotas().get(0).getNota());
    }
}
