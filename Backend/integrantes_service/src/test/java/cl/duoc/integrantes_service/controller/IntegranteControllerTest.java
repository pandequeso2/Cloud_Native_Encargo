package cl.duoc.integrantes_service.controller;

import cl.duoc.integrantes_service.model.Integrante;
import cl.duoc.integrantes_service.service.IntegranteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = IntegranteController.class)
public class IntegranteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IntegranteService integranteService;

    @Autowired
    private ObjectMapper objectMapper;

    private Integrante integranteMock;

    @BeforeEach
    void setUp() {
        integranteMock = new Integrante();
        integranteMock.setIdIntegrante(1);
        integranteMock.setNombre("María");
        integranteMock.setApellido("López");
        integranteMock.setRutCuerpo(12345678);
        integranteMock.setRutDv("9");
        integranteMock.setCorreoElectronico("maria.lopez@example.com");
        integranteMock.setIdRol(1);
        integranteMock.setIdGrupo(2);
        integranteMock.setDisponibilidad(Integrante.Disponibilidad.ALTA);
        integranteMock.setIdNota(3);
    }

    @Test
    void testGetIntegrantes() throws Exception {
        List<Integrante> listaIntegrantes = Arrays.asList(integranteMock);
        when(integranteService.obtenerIntegrantes()).thenReturn(listaIntegrantes);

        mockMvc.perform(get("/api/v1/integrantes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("María"));
    }

    @Test
    void testCrearIntegrante() throws Exception {
        when(integranteService.guardarIntegrante(any(Integrante.class))).thenReturn(integranteMock);

        mockMvc.perform(post("/api/v1/integrantes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(integranteMock)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("María"));
    }

    @Test
    void testGetIntegrantes_CuandoNoExisten() throws Exception {
        when(integranteService.obtenerIntegrantes()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/integrantes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()); 
    }

    @Test
    void testGetIntegrantePorId_Exitoso() throws Exception {
        Integrante mock = new Integrante();
        mock.setIdIntegrante(1);
        mock.setNombre("Juan");

        when(integranteService.buscarIntegrantePorId(1)).thenReturn(Optional.of(mock));

        mockMvc.perform(get("/api/v1/integrantes/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan"));
    }

    @Test
    void testGetIntegrantePorId_NoExiste() throws Exception {
        when(integranteService.buscarIntegrantePorId(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/integrantes/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}