package cl.duoc.entregas_service.service;

import cl.duoc.entregas_service.model.Entrega;
import cl.duoc.entregas_service.repository.EntregaRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EntregaServiceTest {

    @Mock
    private EntregaRepository repository;

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
    }
}
