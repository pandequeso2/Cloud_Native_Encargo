package cl.duoc.comentarios_service.service;

import cl.duoc.comentarios_service.model.Comentario;
import cl.duoc.comentarios_service.repository.ComentarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ComentarioServiceTest {

    @Mock
    private ComentarioRepository repository;

    @InjectMocks
    private ComentarioService service;

    private Comentario comentarioMock;

    @BeforeEach
    void setUp() {
        comentarioMock = new Comentario();
        comentarioMock.setIdComentario(1);
        comentarioMock.setIdEntrega(2);
        comentarioMock.setIdProfesor(3);
        comentarioMock.setContenido("Excelente trabajo");
        comentarioMock.setFechaComentario(LocalDateTime.now());
        comentarioMock.setTipoComentario(Comentario.TipoComentario.FELICITACION);
    }

    @Test
    void testListarTodos() {
        when(repository.findAll()).thenReturn(Arrays.asList(comentarioMock));

        List<Comentario> resultado = service.listarTodos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Excelente trabajo", resultado.get(0).getContenido());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testBuscarPorId() {
        when(repository.findById(1)).thenReturn(Optional.of(comentarioMock));

        Optional<Comentario> resultado = service.buscarPorId(1);

        assertTrue(resultado.isPresent());
        assertEquals("Excelente trabajo", resultado.get().getContenido());
        verify(repository, times(1)).findById(1);
    }

    @Test
    void testGuardar() {
        when(repository.save(any(Comentario.class))).thenReturn(comentarioMock);

        Comentario resultado = service.guardar(comentarioMock);

        assertNotNull(resultado);
        assertEquals("Excelente trabajo", resultado.getContenido());
        verify(repository, times(1)).save(any(Comentario.class));
    }
}
