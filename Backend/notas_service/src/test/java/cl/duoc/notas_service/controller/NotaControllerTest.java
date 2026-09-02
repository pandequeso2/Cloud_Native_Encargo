package cl.duoc.notas_service.controller;

import cl.duoc.notas_service.model.Nota;
import cl.duoc.notas_service.model.Ponderacion;
import cl.duoc.notas_service.service.NotaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = NotaController.class)
public class NotaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotaService notaService;

    @Autowired
    private ObjectMapper objectMapper;

    private Nota notaMock;

    @BeforeEach
    void setUp() {
        Ponderacion ponderacion = new Ponderacion();
        ponderacion.setIdPonderacion(1);
        ponderacion.setPorcentaje(0.4);

        notaMock = new Nota();
        notaMock.setIdNota(1);
        notaMock.setNota(5.8);
        notaMock.setPonderacion(ponderacion);
        notaMock.setIdIntegrante(3);
    }

    @Test
    void testObtenerNotas() throws Exception {
        when(notaService.obtenerNotas()).thenReturn(Arrays.asList(notaMock));

        mockMvc.perform(get("/api/v1/notas")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nota").value(5.8));
    }

    @Test
    void testObtenerNota() throws Exception {
        when(notaService.buscarNotaPorId(1)).thenReturn(Optional.of(notaMock));

        mockMvc.perform(get("/api/v1/notas/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nota").value(5.8));
    }

    @Test
    void testCrearNota() throws Exception {
        when(notaService.guardarNota(any(Nota.class))).thenReturn(notaMock);

        mockMvc.perform(post("/api/v1/notas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notaMock)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nota").value(5.8));
    }
}
