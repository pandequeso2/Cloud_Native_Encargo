package cl.duoc.trabajos_service.controller;

import cl.duoc.trabajos_service.model.Trabajo;
import cl.duoc.trabajos_service.service.TrabajoService;
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

@WebMvcTest(controllers = TrabajoController.class)
public class TrabajoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TrabajoService service;

    @Autowired
    private ObjectMapper objectMapper;

    private Trabajo trabajoMock;

    @BeforeEach
    void setUp() {
        trabajoMock = new Trabajo();
        trabajoMock.setIdTrabajo(1);
        trabajoMock.setNombreTrabajo("Proyecto 1");
        trabajoMock.setPorcentajeNota(0.3);
        trabajoMock.setIdGrupo(4);
        trabajoMock.setTipoTrabajo(Trabajo.TipoTrabajo.PRESENTACION);
        trabajoMock.setSemestre(2);
        trabajoMock.setEstado(Trabajo.Estado.ENTREGADO);
    }

    @Test
    void testObtenerTrabajos() throws Exception {
        when(service.obtenerTrabajos()).thenReturn(Arrays.asList(trabajoMock));

        mockMvc.perform(get("/api/v1/trabajos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreTrabajo").value("Proyecto 1"));
    }

    @Test
    void testObtenerTrabajo() throws Exception {
        when(service.buscarTrabajoPorId(1)).thenReturn(Optional.of(trabajoMock));

        mockMvc.perform(get("/api/v1/trabajos/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreTrabajo").value("Proyecto 1"));
    }

    @Test
    void testCrearTrabajo() throws Exception {
        when(service.guardarTrabajo(any(Trabajo.class))).thenReturn(trabajoMock);

        mockMvc.perform(post("/api/v1/trabajos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(trabajoMock)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreTrabajo").value("Proyecto 1"));
    }
}
