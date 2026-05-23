package br.com.fiap.clyvovet.controller;

import br.com.fiap.clyvovet.dto.ConsultaDTO;
import br.com.fiap.clyvovet.exception.GlobalExceptionHandler;
import br.com.fiap.clyvovet.exception.ResourceNotFoundException;
import br.com.fiap.clyvovet.model.Consulta.StatusConsulta;
import br.com.fiap.clyvovet.model.Consulta.TipoConsulta;
import br.com.fiap.clyvovet.service.ConsultaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConsultaController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("ConsultaController - Testes de Integração Web")
class ConsultaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConsultaService consultaService;

    private ObjectMapper objectMapper;
    private ConsultaDTO.Response responseDTO;
    private ConsultaDTO.Request requestDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        responseDTO = ConsultaDTO.Response.builder()
                .id(1L).dataConsulta(LocalDate.now().plusDays(5))
                .status(StatusConsulta.AGENDADA).tipoConsulta(TipoConsulta.ROTINA)
                .veterinario("Dr. Paulo").valor(200.0).build();

        requestDTO = ConsultaDTO.Request.builder()
                .dataConsulta(LocalDate.now().plusDays(5))
                .status(StatusConsulta.AGENDADA).tipoConsulta(TipoConsulta.ROTINA)
                .veterinario("Dr. Paulo").valor(200.0)
                .petId(1L).clinicaId(1L).build();
    }

    @Test
    @DisplayName("GET /api/v1/consultas/pet/{petId} - deve retornar consultas do pet")
    void listarPorPet_deveRetornar200() throws Exception {
        Page<ConsultaDTO.Response> page = new PageImpl<>(List.of(responseDTO));
        when(consultaService.listarPorPet(eq(1L), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/consultas/pet/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].veterinario").value("Dr. Paulo"));
    }

    @Test
    @DisplayName("GET /api/v1/consultas/{id} - deve retornar 200 quando consulta existe")
    void buscarPorId_deveRetornar200() throws Exception {
        when(consultaService.buscarPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/consultas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("AGENDADA"));
    }

    @Test
    @DisplayName("GET /api/v1/consultas/{id} - deve retornar 404 quando não encontrada")
    void buscarPorId_deveRetornar404() throws Exception {
        when(consultaService.buscarPorId(99L))
                .thenThrow(new ResourceNotFoundException("Consulta", 99L));

        mockMvc.perform(get("/api/v1/consultas/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/v1/consultas - deve retornar 201 ao criar consulta válida")
    void criar_deveRetornar201() throws Exception {
        when(consultaService.criar(any(ConsultaDTO.Request.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/consultas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("AGENDADA"));
    }

    @Test
    @DisplayName("PATCH /api/v1/consultas/{id}/cancelar - deve retornar 204")
    void cancelar_deveRetornar204() throws Exception {
        doNothing().when(consultaService).cancelar(1L);

        mockMvc.perform(patch("/api/v1/consultas/1/cancelar"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/v1/consultas/status/{status} - deve filtrar por status")
    void listarPorStatus_deveRetornar200() throws Exception {
        Page<ConsultaDTO.Response> page = new PageImpl<>(List.of(responseDTO));
        when(consultaService.listarPorStatus(eq(StatusConsulta.AGENDADA), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/consultas/status/AGENDADA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status").value("AGENDADA"));
    }
}
