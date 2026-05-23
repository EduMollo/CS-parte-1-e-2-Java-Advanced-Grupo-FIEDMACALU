package br.com.fiap.clyvovet.controller;

import br.com.fiap.clyvovet.dto.TutorDTO;
import br.com.fiap.clyvovet.exception.GlobalExceptionHandler;
import br.com.fiap.clyvovet.exception.ResourceNotFoundException;
import br.com.fiap.clyvovet.service.TutorService;
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

@WebMvcTest(TutorController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("TutorController - Testes de Integração Web")
class TutorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TutorService tutorService;

    private ObjectMapper objectMapper;
    private TutorDTO.Response responseDTO;
    private TutorDTO.Request requestDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        responseDTO = TutorDTO.Response.builder()
                .id(1L).nome("Maria Silva").cpf("12345678901")
                .email("maria@email.com").telefone("11987654321")
                .dataNascimento(LocalDate.of(1990, 5, 15))
                .ativo(true).totalPets(0).build();

        requestDTO = TutorDTO.Request.builder()
                .nome("Maria Silva").cpf("12345678901")
                .email("maria@email.com").telefone("11987654321")
                .dataNascimento(LocalDate.of(1990, 5, 15)).build();
    }

    @Test
    @DisplayName("GET /api/v1/tutores - deve retornar 200 com página de tutores")
    void listar_deveRetornar200() throws Exception {
        Page<TutorDTO.Response> page = new PageImpl<>(List.of(responseDTO));
        when(tutorService.listarAtivos(any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/tutores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nome").value("Maria Silva"));
    }

    @Test
    @DisplayName("GET /api/v1/tutores/{id} - deve retornar 200 quando tutor existe")
    void buscarPorId_deveRetornar200() throws Exception {
        when(tutorService.buscarPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/tutores/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cpf").value("12345678901"));
    }

    @Test
    @DisplayName("GET /api/v1/tutores/{id} - deve retornar 404 quando tutor não existe")
    void buscarPorId_deveRetornar404() throws Exception {
        when(tutorService.buscarPorId(99L))
                .thenThrow(new ResourceNotFoundException("Tutor", 99L));

        mockMvc.perform(get("/api/v1/tutores/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/v1/tutores - deve retornar 201 ao criar tutor válido")
    void criar_deveRetornar201() throws Exception {
        when(tutorService.criar(any(TutorDTO.Request.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/tutores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Maria Silva"));
    }

    @Test
    @DisplayName("POST /api/v1/tutores - deve retornar 400 com dados inválidos")
    void criar_deveRetornar400QuandoDadosInvalidos() throws Exception {
        TutorDTO.Request requestInvalido = TutorDTO.Request.builder()
                .nome("").cpf("123").email("email-invalido").build();

        mockMvc.perform(post("/api/v1/tutores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/v1/tutores/{id} - deve retornar 200 ao atualizar")
    void atualizar_deveRetornar200() throws Exception {
        when(tutorService.atualizar(eq(1L), any(TutorDTO.Request.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/v1/tutores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("DELETE /api/v1/tutores/{id} - deve retornar 204")
    void inativar_deveRetornar204() throws Exception {
        doNothing().when(tutorService).inativar(1L);

        mockMvc.perform(delete("/api/v1/tutores/1"))
                .andExpect(status().isNoContent());
    }
}
