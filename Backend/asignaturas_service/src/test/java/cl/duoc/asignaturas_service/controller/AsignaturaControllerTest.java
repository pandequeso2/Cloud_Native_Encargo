package cl.duoc.asignaturas_service.controller;

import cl.duoc.asignaturas_service.model.Asignatura;
import cl.duoc.asignaturas_service.service.AsignaturaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AsignaturaController.class)
public class AsignaturaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AsignaturaService asignaturaService;

    @Autowired
    private ObjectMapper objectMapper;

    private Asignatura asignaturaMock;

    @BeforeEach
    void setUp() {
        asignaturaMock = new Asignatura();
        asignaturaMock.setIdAsignatura(1);
        asignaturaMock.setNombreAsignatura("Programación Java");
        asignaturaMock.setIdProfesor(10);
    }

    @Test
    void testGetAsignaturas() throws Exception {
        List<Asignatura> listaAsignaturas = Arrays.asList(asignaturaMock);
        when(asignaturaService.obtenerAsignaturas()).thenReturn(listaAsignaturas);

        mockMvc.perform(get("/api/v1/asignaturas")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreAsignatura").value("Programación Java"));
    }

    @Test
    void testCrearAsignatura() throws Exception {
        when(asignaturaService.guardarAsignatura(any(Asignatura.class))).thenReturn(asignaturaMock);

        mockMvc.perform(post("/api/v1/asignaturas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(asignaturaMock)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreAsignatura").value("Programación Java"));
    }
}
