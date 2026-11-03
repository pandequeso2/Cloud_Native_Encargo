package cl.duoc.entregas_service.service;

import cl.duoc.entregas_service.model.Entrega;
import cl.duoc.entregas_service.repository.EntregaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EntregaServiceTest {

    @Mock
    private EntregaRepository repository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private EntregaService service;

    private Entrega entregaMock;

    @BeforeEach
    void setUp() {
        entregaMock = new Entrega();
        entregaMock.setIdEntrega(1);
        entregaMock.setIdGrupo(2);
        entregaMock.setIdTrabajo(3);
        entregaMock.setFechaEntrega(LocalDate.now());
        entregaMock.setEstado(Entrega.Estado.ENTREGADO);
        entregaMock.setEstudiante("Estudiante Duoc");
    }

    @Test
    void testListarTodos() {
        when(repository.findAll()).thenReturn(Arrays.asList(entregaMock));

        List<Entrega> resultado = service.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(2, resultado.get(0).getIdGrupo());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testBuscarPorId() {
        when(repository.findById(1)).thenReturn(Optional.of(entregaMock));

        Optional<Entrega> resultado = service.buscarPorId(1);

        assertTrue(resultado.isPresent());
        assertEquals(3, resultado.get().getIdTrabajo());
        verify(repository, times(1)).findById(1);
    }

    @Test
    void testGuardar() {
        when(repository.save(any(Entrega.class))).thenReturn(entregaMock);

        Entrega resultado = service.guardar(entregaMock);

        assertNotNull(resultado);
        assertEquals(Entrega.Estado.ENTREGADO, resultado.getEstado());
        verify(repository, times(1)).save(any(Entrega.class));
        verify(rabbitTemplate, times(1)).convertAndSend(eq("orders.exchange"), eq("order.created"), anyString());
    }

    @Test
    void testActualizar() {
        when(repository.existsById(1)).thenReturn(true);
        when(repository.save(any(Entrega.class))).thenReturn(entregaMock);

        Entrega resultado = service.actualizar(1, entregaMock);

        assertNotNull(resultado);
        assertEquals(1, resultado.getIdEntrega());
        verify(repository, times(1)).save(any(Entrega.class));
        verify(rabbitTemplate, times(1)).convertAndSend(eq("orders.exchange"), eq("order.created"), anyString());
    }
}
