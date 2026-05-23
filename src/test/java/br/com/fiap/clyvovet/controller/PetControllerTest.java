package br.com.fiap.clyvovet.controller;

import br.com.fiap.clyvovet.dto.PetDTO;
import br.com.fiap.clyvovet.dto.SaudePetDashboardDTO;
import br.com.fiap.clyvovet.exception.GlobalExceptionHandler;
import br.com.fiap.clyvovet.exception.ResourceNotFoundException;
import br.com.fiap.clyvovet.model.Pet;
import br.com.fiap.clyvovet.service.PetService;
import br.com.fiap.clyvovet.service.SaudePetService;
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

@WebMvcTest(PetController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("PetController - Testes de Integração Web")
class PetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PetService petService;

    @MockBean
    private SaudePetService saudePetService;

    private ObjectMapper objectMapper;
    private PetDTO.Response responseDTO;
    private PetDTO.Request requestDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        responseDTO = PetDTO.Response.builder()
                .id(1L).nome("Rex").especie(Pet.Especie.CACHORRO).raca("Labrador")
                .dataNascimento(LocalDate.of(2020, 3, 10)).pesoKg(25.0)
                .sexo(Pet.Sexo.MACHO).castrado(false).ativo(true).build();

        requestDTO = PetDTO.Request.builder()
                .nome("Rex").especie(Pet.Especie.CACHORRO).raca("Labrador")
                .dataNascimento(LocalDate.of(2020, 3, 10)).pesoKg(25.0)
                .sexo(Pet.Sexo.MACHO).castrado(false).tutorId(1L).build();
    }

    @Test
    @DisplayName("GET /api/v1/pets - deve retornar 200 com lista de pets")
    void listar_deveRetornar200() throws Exception {
        Page<PetDTO.Response> page = new PageImpl<>(List.of(responseDTO));
        when(petService.listarAtivos(any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/pets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nome").value("Rex"));
    }

    @Test
    @DisplayName("GET /api/v1/pets/{id} - deve retornar 200 quando pet existe")
    void buscarPorId_deveRetornar200() throws Exception {
        when(petService.buscarPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/pets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.especie").value("CACHORRO"));
    }

    @Test
    @DisplayName("GET /api/v1/pets/{id} - deve retornar 404 quando pet não existe")
    void buscarPorId_deveRetornar404() throws Exception {
        when(petService.buscarPorId(99L))
                .thenThrow(new ResourceNotFoundException("Pet", 99L));

        mockMvc.perform(get("/api/v1/pets/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/v1/pets/tutor/{tutorId} - deve retornar lista de pets do tutor")
    void buscarPorTutor_deveRetornar200() throws Exception {
        when(petService.buscarPorTutor(1L)).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/v1/pets/tutor/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Rex"));
    }

    @Test
    @DisplayName("GET /api/v1/pets/{id}/dashboard - deve retornar dashboard de saúde")
    void dashboard_deveRetornar200() throws Exception {
        SaudePetDashboardDTO dashboard = SaudePetDashboardDTO.builder()
                .petId(1L).petNome("Rex").scoreRisco(85).classificacaoRisco("BAIXO").build();
        when(saudePetService.gerarDashboard(1L)).thenReturn(dashboard);

        mockMvc.perform(get("/api/v1/pets/1/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scoreRisco").value(85));
    }

    @Test
    @DisplayName("POST /api/v1/pets - deve retornar 201 ao criar pet válido")
    void criar_deveRetornar201() throws Exception {
        when(petService.criar(any(PetDTO.Request.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Rex"));
    }

    @Test
    @DisplayName("DELETE /api/v1/pets/{id} - deve retornar 204")
    void inativar_deveRetornar204() throws Exception {
        doNothing().when(petService).inativar(1L);

        mockMvc.perform(delete("/api/v1/pets/1"))
                .andExpect(status().isNoContent());
    }
}
