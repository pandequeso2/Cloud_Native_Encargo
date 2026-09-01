package cl.duoc.notas_service.controller;

import cl.duoc.notas_service.model.Ponderacion;
import cl.duoc.notas_service.service.PonderacionService;
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

@WebMvcTest(controllers = PonderacionController.class)
public class PonderacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PonderacionService service;

    @Autowired
    private ObjectMapper objectMapper;

    private Ponderacion ponderacionMock;

    @BeforeEach
    void setUp() {
        ponderacionMock = new Ponderacion();
        ponderacionMock.setIdPonderacion(1);
        ponderacionMock.setPorcentaje(0.15);
    }

    @Test
    void testObtenerPonderaciones() throws Exception {
        when(service.obtenerPonderacion()).thenReturn(Arrays.asList(ponderacionMock));

        mockMvc.perform(get("/api/v1/ponderaciones")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].porcentaje").value(0.15));
    }

    @Test
    void testObtenerPonderacion() throws Exception {
        when(service.buscarPonderacionPorId(1)).thenReturn(Optional.of(ponderacionMock));

        mockMvc.perform(get("/api/v1/ponderaciones/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.porcentaje").value(0.15));
    }

    @Test
    void testCrearPonderacion() throws Exception {
        when(service.guardarPonderacion(any(Ponderacion.class))).thenReturn(ponderacionMock);

        mockMvc.perform(post("/api/v1/ponderaciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ponderacionMock)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.porcentaje").value(0.15));
    }
}
