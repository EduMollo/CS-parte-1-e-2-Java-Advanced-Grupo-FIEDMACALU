package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.ClinicaDTO;
import br.com.fiap.clyvovet.exception.BusinessException;
import br.com.fiap.clyvovet.exception.ResourceNotFoundException;
import br.com.fiap.clyvovet.model.Clinica;
import br.com.fiap.clyvovet.repository.ClinicaRepository;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClinicaService - Testes Unitários")
class ClinicaServiceTest {

    @Mock
    private ClinicaRepository clinicaRepository;

    @InjectMocks
    private ClinicaService clinicaService;

    private Clinica clinica;
    private ClinicaDTO.Request requestDTO;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        clinica = Clinica.builder()
                .id(1L).nome("Clínica VetLife").cnpj("12345678000100")
                .telefone("1134567890").email("vet@vetlife.com")
                .endereco("Rua das Flores, 100").cidade("São Paulo").estado("SP").ativo(true).build();

        requestDTO = ClinicaDTO.Request.builder()
                .nome("Clínica VetLife").cnpj("12345678000100")
                .telefone("1134567890").email("vet@vetlife.com")
                .endereco("Rua das Flores, 100").cidade("São Paulo").estado("SP").build();

        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("listarAtivas - deve retornar página de clínicas ativas")
    void listarAtivas_deveRetornarPaginaDeClinicas() {
        Page<Clinica> page = new PageImpl<>(List.of(clinica));
        when(clinicaRepository.findByAtivoTrue(pageable)).thenReturn(page);

        Page<ClinicaDTO.Response> result = clinicaService.listarAtivas(pageable);

        assertThat(result).isNotEmpty();
        assertThat(result.getContent().get(0).getNome()).isEqualTo("Clínica VetLife");
    }

    @Test
    @DisplayName("buscarPorId - deve retornar clínica existente")
    void buscarPorId_deveRetornarClinicaExistente() {
        when(clinicaRepository.findById(1L)).thenReturn(Optional.of(clinica));

        ClinicaDTO.Response result = clinicaService.buscarPorId(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCnpj()).isEqualTo("12345678000100");
    }

    @Test
    @DisplayName("buscarPorId - deve lançar exceção quando não encontrada")
    void buscarPorId_deveLancarExcecaoQuandoNaoEncontrada() {
        when(clinicaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clinicaService.buscarPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("criar - deve criar clínica com sucesso quando CNPJ único")
    void criar_deveCriarClinicaComSucesso() {
        when(clinicaRepository.existsByCnpj("12345678000100")).thenReturn(false);
        when(clinicaRepository.save(any(Clinica.class))).thenReturn(clinica);

        ClinicaDTO.Response result = clinicaService.criar(requestDTO);

        assertThat(result.getNome()).isEqualTo("Clínica VetLife");
        verify(clinicaRepository).save(any(Clinica.class));
    }

    @Test
    @DisplayName("criar - deve lançar BusinessException quando CNPJ duplicado")
    void criar_deveLancarExcecaoQuandoCnpjDuplicado() {
        when(clinicaRepository.existsByCnpj("12345678000100")).thenReturn(true);

        assertThatThrownBy(() -> clinicaService.criar(requestDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("CNPJ");

        verify(clinicaRepository, never()).save(any());
    }

    @Test
    @DisplayName("inativar - deve desativar clínica (soft delete)")
    void inativar_deveDesativarClinica() {
        when(clinicaRepository.findById(1L)).thenReturn(Optional.of(clinica));
        when(clinicaRepository.save(any(Clinica.class))).thenReturn(clinica);

        clinicaService.inativar(1L);

        assertThat(clinica.isAtivo()).isFalse();
        verify(clinicaRepository).save(clinica);
    }

}
