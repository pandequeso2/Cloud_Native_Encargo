package cl.duoc.entregas_service.controller;

import cl.duoc.entregas_service.model.Entrega;
import cl.duoc.entregas_service.service.EntregaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EntregaController.class)
public class EntregaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EntregaService service;

    @Autowired
    private ObjectMapper objectMapper;

    private Entrega entregaMock;

    @BeforeEach
    void setUp() {
        entregaMock = new Entrega();
        entregaMock.setIdEntrega(1);
        entregaMock.setIdGrupo(2);
        entregaMock.setIdTrabajo(3);
        entregaMock.setFechaEntrega(LocalDate.now());
        entregaMock.setEstado(Entrega.Estado.PENDIENTE);
    }

    @Test
    void testListar() throws Exception {
        when(service.listarTodos()).thenReturn(Arrays.asList(entregaMock));

        mockMvc.perform(get("/api/v1/entregas")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idGrupo").value(2));
    }

    @Test
    void testObtenerPorId() throws Exception {
        when(service.buscarPorId(1)).thenReturn(Optional.of(entregaMock));

        mockMvc.perform(get("/api/v1/entregas/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idTrabajo").value(3));
    }

    @Test
    void testCrear() throws Exception {
        when(service.guardar(any(Entrega.class))).thenReturn(entregaMock);

        mockMvc.perform(post("/api/v1/entregas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(entregaMock)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idGrupo").value(2));
    }
}
