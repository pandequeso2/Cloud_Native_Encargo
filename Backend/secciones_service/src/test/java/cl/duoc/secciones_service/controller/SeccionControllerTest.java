package cl.duoc.secciones_service.controller;

import cl.duoc.secciones_service.model.Seccion;
import cl.duoc.secciones_service.service.SeccionService;
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

@WebMvcTest(controllers = SeccionController.class)
public class SeccionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SeccionService service;

    @Autowired
    private ObjectMapper objectMapper;

    private Seccion seccionMock;

    @BeforeEach
    void setUp() {
        seccionMock = new Seccion();
        seccionMock.setIdSeccion(1);
        seccionMock.setCodigoSeccion("INF-303");
        seccionMock.setCapacidadMaxima(25);
        seccionMock.setIdAsignatura(4);
        seccionMock.setIdProfesor(5);
    }

    @Test
    void testObtenerSecciones() throws Exception {
        when(service.obtenerSecciones()).thenReturn(Arrays.asList(seccionMock));

        mockMvc.perform(get("/api/v1/secciones")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigoSeccion").value("INF-303"));
    }

    @Test
    void testObtenerSeccion() throws Exception {
        when(service.buscarSeccionPorId(1)).thenReturn(Optional.of(seccionMock));

        mockMvc.perform(get("/api/v1/secciones/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoSeccion").value("INF-303"));
    }

    @Test
    void testCrearSeccion() throws Exception {
        when(service.guardarSeccion(any(Seccion.class))).thenReturn(seccionMock);

        mockMvc.perform(post("/api/v1/secciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(seccionMock)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoSeccion").value("INF-303"));
    }
}
