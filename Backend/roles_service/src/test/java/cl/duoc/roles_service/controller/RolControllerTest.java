package cl.duoc.roles_service.controller;

import cl.duoc.roles_service.model.Rol;
import cl.duoc.roles_service.service.RolService;
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

@WebMvcTest(controllers = RolController.class)
public class RolControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RolService rolService;

    @Autowired
    private ObjectMapper objectMapper;

    private Rol rolMock;

    @BeforeEach
    void setUp() {
        rolMock = new Rol();
        rolMock.setIdRol(1);
        rolMock.setRol("PROFESOR");
    }

    @Test
    void testGetRoles() throws Exception {
        List<Rol> listaRoles = Arrays.asList(rolMock);
        when(rolService.listarTodos()).thenReturn(listaRoles);

        mockMvc.perform(get("/api/v1/roles")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rol").value("PROFESOR"));
    }

    @Test
    void testCrearRol() throws Exception {
        when(rolService.guardar(any(Rol.class))).thenReturn(rolMock);

        mockMvc.perform(post("/api/v1/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rolMock)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rol").value("PROFESOR"));
    }
}