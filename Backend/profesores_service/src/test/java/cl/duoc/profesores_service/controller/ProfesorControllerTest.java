package cl.duoc.profesores_service.controller;

import cl.duoc.profesores_service.model.Profesor;
import cl.duoc.profesores_service.service.ProfesorService;
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

@WebMvcTest(controllers = ProfesorController.class)
public class ProfesorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProfesorService service;

    @Autowired
    private ObjectMapper objectMapper;

    private Profesor profesorMock;

    @BeforeEach
    void setUp() {
        profesorMock = new Profesor();
        profesorMock.setIdProfesor(1);
        profesorMock.setNombreProfesor("Luis");
        profesorMock.setApellidoProfesor("Jara");
        profesorMock.setRutCuerpo(12345678);
        profesorMock.setRutDv("K");
        profesorMock.setCorreoElectronico("luis.jara@example.com");
    }

    @Test
    void testObtenerProfesores() throws Exception {
        when(service.obtenerProfesores()).thenReturn(Arrays.asList(profesorMock));

        mockMvc.perform(get("/api/v1/profesores")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreProfesor").value("Luis"));
    }

    @Test
    void testObtenerProfesor() throws Exception {
        when(service.buscarProfesorPorId(1)).thenReturn(Optional.of(profesorMock));

        mockMvc.perform(get("/api/v1/profesores/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreProfesor").value("Luis"));
    }

    @Test
    void testCrearProfesor() throws Exception {
        when(service.guardarProfesor(any(Profesor.class))).thenReturn(profesorMock);

        mockMvc.perform(post("/api/v1/profesores")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profesorMock)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombreProfesor").value("Luis"));
    }
}
