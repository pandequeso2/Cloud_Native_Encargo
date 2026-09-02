package cl.duoc.asignaturas_service.controller;

import cl.duoc.asignaturas_service.model.Unidad;
import cl.duoc.asignaturas_service.service.UnidadService;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UnidadController.class)
public class UnidadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UnidadService unidadService;

    @Autowired
    private ObjectMapper objectMapper;

    private Unidad unidadMock;

    @BeforeEach
    void setUp() {
        unidadMock = new Unidad();
        unidadMock.setIdUnidad(1);
        unidadMock.setIdAsignatura(1);
        unidadMock.setNumeroUnidad(1);
        unidadMock.setNombreUnidad("Usar print");
    }

    @Test
    void testGetUnidades() throws Exception {
        List<Unidad> listaUnidades = Arrays.asList(unidadMock);
        when(unidadService.obtenerUnidades()).thenReturn(listaUnidades);

        mockMvc.perform(get("/api/v1/unidades")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreUnidad").value("Usar print"));
    }

    @Test
    void testGetUnidades_sinContenido() throws Exception {
        when(unidadService.obtenerUnidades()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/v1/unidades")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetUnidadPorId_existente() throws Exception {
        when(unidadService.buscarUnidadPorId(1)).thenReturn(Optional.of(unidadMock));

        mockMvc.perform(get("/api/v1/unidades/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreUnidad").value("Usar print"));
    }

    @Test
    void testGetUnidadPorId_noExistente() throws Exception {
        when(unidadService.buscarUnidadPorId(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/unidades/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCrearUnidad() throws Exception {
        when(unidadService.guardarUnidad(any(Unidad.class))).thenReturn(unidadMock);

        mockMvc.perform(post("/api/v1/unidades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(unidadMock)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreUnidad").value("Usar print"));
    }

    @Test
    void testActualizarUnidad_existente() throws Exception {
        when(unidadService.actualizarUnidades(any(Integer.class), any(Unidad.class))).thenReturn(unidadMock);

        mockMvc.perform(put("/api/v1/unidades/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(unidadMock)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreUnidad").value("Usar print"));
    }

    @Test
    void testActualizarUnidad_noExistente() throws Exception {
        when(unidadService.actualizarUnidades(any(Integer.class), any(Unidad.class)))
                .thenThrow(new RuntimeException("La unidad con ID 99 no existe"));

        mockMvc.perform(put("/api/v1/unidades/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(unidadMock)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testEliminarUnidad_existente() throws Exception {
        doNothing().when(unidadService).eliminarUnidad(1);

        mockMvc.perform(delete("/api/v1/unidades/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testEliminarUnidad_noExistente() throws Exception {
        doThrowRuntimeExceptionOnEliminar(99);

        mockMvc.perform(delete("/api/v1/unidades/99"))
                .andExpect(status().isNotFound());
    }

    private void doThrowRuntimeExceptionOnEliminar(int id) {
        org.mockito.Mockito.doThrow(new RuntimeException("no existe una unidad con el id " + id))
                .when(unidadService).eliminarUnidad(id);
    }
}