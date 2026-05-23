package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.ConsultaDTO;
import br.com.fiap.clyvovet.exception.BusinessException;
import br.com.fiap.clyvovet.exception.ResourceNotFoundException;
import br.com.fiap.clyvovet.model.Clinica;
import br.com.fiap.clyvovet.model.Consulta;
import br.com.fiap.clyvovet.model.Consulta.StatusConsulta;
import br.com.fiap.clyvovet.model.Consulta.TipoConsulta;
import br.com.fiap.clyvovet.model.Pet;
import br.com.fiap.clyvovet.model.Tutor;
import br.com.fiap.clyvovet.repository.ClinicaRepository;
import br.com.fiap.clyvovet.repository.ConsultaRepository;
import br.com.fiap.clyvovet.repository.PetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConsultaService - Testes Unitários")
class ConsultaServiceTest {

    @Mock
    private ConsultaRepository consultaRepository;
    @Mock
    private PetRepository petRepository;
    @Mock
    private ClinicaRepository clinicaRepository;

    @InjectMocks
    private ConsultaService consultaService;

    private Consulta consulta;
    private Pet pet;
    private Clinica clinica;
    private ConsultaDTO.Request requestDTO;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        Tutor tutor = Tutor.builder().id(1L).nome("Maria Silva").build();

        clinica = Clinica.builder()
                .id(1L).nome("Clínica VetLife").cnpj("12345678000100")
                .telefone("1134567890").cidade("São Paulo").estado("SP").ativo(true).build();

        pet = Pet.builder()
                .id(1L).nome("Rex").especie(Pet.Especie.CACHORRO).raca("Labrador")
                .dataNascimento(LocalDate.of(2020, 3, 10)).ativo(true)
                .tutor(tutor).clinicaPrincipal(clinica).build();

        consulta = Consulta.builder()
                .id(1L).dataConsulta(LocalDate.now().plusDays(5))
                .status(StatusConsulta.AGENDADA).tipoConsulta(TipoConsulta.ROTINA)
                .veterinario("Dr. Paulo").valor(200.0)
                .pet(pet).clinica(clinica).build();

        requestDTO = ConsultaDTO.Request.builder()
                .dataConsulta(LocalDate.now().plusDays(5))
                .status(StatusConsulta.AGENDADA).tipoConsulta(TipoConsulta.ROTINA)
                .veterinario("Dr. Paulo").valor(200.0)
                .petId(1L).clinicaId(1L).build();

        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("listarPorPet - deve retornar consultas do pet")
    void listarPorPet_deveRetornarConsultasDoPet() {
        Page<Consulta> page = new PageImpl<>(List.of(consulta));
        when(consultaRepository.findByPetId(1L, pageable)).thenReturn(page);

        Page<ConsultaDTO.Response> result = consultaService.listarPorPet(1L, pageable);

        assertThat(result).isNotEmpty();
        assertThat(result.getContent().get(0).getVeterinario()).isEqualTo("Dr. Paulo");
    }

    @Test
    @DisplayName("buscarPorId - deve retornar consulta quando ID existe")
    void buscarPorId_deveRetornarConsultaExistente() {
        when(consultaRepository.findById(1L)).thenReturn(Optional.of(consulta));

        ConsultaDTO.Response result = consultaService.buscarPorId(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(StatusConsulta.AGENDADA);
    }

    @Test
    @DisplayName("buscarPorId - deve lançar exceção quando consulta não encontrada")
    void buscarPorId_deveLancarExcecaoQuandoNaoEncontrada() {
        when(consultaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> consultaService.buscarPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("criar - deve criar consulta com sucesso")
    void criar_deveCriarConsultaComSucesso() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(pet));
        when(clinicaRepository.findById(1L)).thenReturn(Optional.of(clinica));
        when(consultaRepository.save(any(Consulta.class))).thenReturn(consulta);

        ConsultaDTO.Response result = consultaService.criar(requestDTO);

        assertThat(result.getStatus()).isEqualTo(StatusConsulta.AGENDADA);
        verify(consultaRepository).save(any(Consulta.class));
    }

    @Test
    @DisplayName("criar - deve lançar exceção quando pet não encontrado")
    void criar_deveLancarExcecaoQuandoPetNaoEncontrado() {
        when(petRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> consultaService.criar(requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Pet");
    }

    @Test
    @DisplayName("atualizar - deve lançar BusinessException para consulta cancelada")
    void atualizar_deveLancarExcecaoParaConsultaCancelada() {
        consulta.setStatus(StatusConsulta.CANCELADA);
        when(consultaRepository.findById(1L)).thenReturn(Optional.of(consulta));

        assertThatThrownBy(() -> consultaService.atualizar(1L, requestDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("cancelada");
    }

    @Test
    @DisplayName("cancelar - deve cancelar consulta agendada com sucesso")
    void cancelar_deveCancelarConsultaAgendada() {
        when(consultaRepository.findById(1L)).thenReturn(Optional.of(consulta));
        when(consultaRepository.save(any(Consulta.class))).thenReturn(consulta);

        consultaService.cancelar(1L);

        assertThat(consulta.getStatus()).isEqualTo(StatusConsulta.CANCELADA);
        verify(consultaRepository).save(consulta);
    }

    @Test
    @DisplayName("cancelar - deve lançar BusinessException quando consulta já realizada")
    void cancelar_deveLancarExcecaoParaConsultaRealizada() {
        consulta.setStatus(StatusConsulta.REALIZADA);
        when(consultaRepository.findById(1L)).thenReturn(Optional.of(consulta));

        assertThatThrownBy(() -> consultaService.cancelar(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("realizada");
    }

    @Test
    @DisplayName("listarPorStatus - deve retornar consultas filtradas por status")
    void listarPorStatus_deveRetornarConsultasPorStatus() {
        Page<Consulta> page = new PageImpl<>(List.of(consulta));
        when(consultaRepository.findByStatus(StatusConsulta.AGENDADA, pageable)).thenReturn(page);

        Page<ConsultaDTO.Response> result = consultaService.listarPorStatus(StatusConsulta.AGENDADA, pageable);

        assertThat(result).isNotEmpty();
        assertThat(result.getContent().get(0).getStatus()).isEqualTo(StatusConsulta.AGENDADA);
    }
}
