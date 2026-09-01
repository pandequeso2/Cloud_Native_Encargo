package cl.duoc.comentarios_service.controller;

import cl.duoc.comentarios_service.model.Comentario;
import cl.duoc.comentarios_service.service.ComentarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ComentarioController.class)
public class ComentarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ComentarioService service;

    @Autowired
    private ObjectMapper objectMapper;

    private Comentario comentarioMock;

    @BeforeEach
    void setUp() {
        comentarioMock = new Comentario();
        comentarioMock.setIdComentario(1);
        comentarioMock.setIdEntrega(2);
        comentarioMock.setIdProfesor(3);
        comentarioMock.setContenido("Buen intento");
        comentarioMock.setFechaComentario(LocalDateTime.now());
        comentarioMock.setTipoComentario(Comentario.TipoComentario.FELICITACION);
    }

    @Test
    void testListar() throws Exception {
        when(service.listarTodos()).thenReturn(Arrays.asList(comentarioMock));

        mockMvc.perform(get("/api/v1/comentarios")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].contenido").value("Buen intento"));
    }

    @Test
    void testObtenerPorId() throws Exception {
        when(service.buscarPorId(1)).thenReturn(Optional.of(comentarioMock));

        mockMvc.perform(get("/api/v1/comentarios/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contenido").value("Buen intento"));
    }

    @Test
    void testCrear() throws Exception {
        when(service.guardar(any(Comentario.class))).thenReturn(comentarioMock);

        mockMvc.perform(post("/api/v1/comentarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(comentarioMock)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contenido").value("Buen intento"));
    }
}
