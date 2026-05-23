package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.TutorDTO;
import br.com.fiap.clyvovet.exception.BusinessException;
import br.com.fiap.clyvovet.exception.ResourceNotFoundException;
import br.com.fiap.clyvovet.model.Tutor;
import br.com.fiap.clyvovet.repository.TutorRepository;
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
@DisplayName("TutorService - Testes Unitários")
class TutorServiceTest {

    @Mock
    private TutorRepository tutorRepository;

    @InjectMocks
    private TutorService tutorService;

    private Tutor tutorAtivo;
    private TutorDTO.Request requestDTO;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        tutorAtivo = Tutor.builder()
                .id(1L)
                .nome("Maria Silva")
                .cpf("12345678901")
                .email("maria@email.com")
                .telefone("11987654321")
                .dataNascimento(LocalDate.of(1990, 5, 15))
                .ativo(true)
                .build();

        requestDTO = TutorDTO.Request.builder()
                .nome("Maria Silva")
                .cpf("12345678901")
                .email("maria@email.com")
                .telefone("11987654321")
                .dataNascimento(LocalDate.of(1990, 5, 15))
                .build();

        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("listarAtivos - deve retornar página de tutores ativos")
    void listarAtivos_deveRetornarPaginaDeTutores() {
        Page<Tutor> page = new PageImpl<>(List.of(tutorAtivo));
        when(tutorRepository.findByAtivoTrue(pageable)).thenReturn(page);

        Page<TutorDTO.Response> result = tutorService.listarAtivos(pageable);

        assertThat(result).isNotEmpty();
        assertThat(result.getContent().get(0).getNome()).isEqualTo("Maria Silva");
        verify(tutorRepository).findByAtivoTrue(pageable);
    }

    @Test
    @DisplayName("buscarPorId - deve retornar tutor quando ID existe")
    void buscarPorId_deveRetornarTutorExistente() {
        when(tutorRepository.findById(1L)).thenReturn(Optional.of(tutorAtivo));

        TutorDTO.Response result = tutorService.buscarPorId(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNome()).isEqualTo("Maria Silva");
        assertThat(result.getCpf()).isEqualTo("12345678901");
    }

    @Test
    @DisplayName("buscarPorId - deve lançar ResourceNotFoundException quando ID não existe")
    void buscarPorId_deveLancarExcecaoQuandoNaoEncontrado() {
        when(tutorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tutorService.buscarPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("buscarPorCpf - deve retornar tutor quando CPF existe")
    void buscarPorCpf_deveRetornarTutorExistente() {
        when(tutorRepository.findByCpf("12345678901")).thenReturn(Optional.of(tutorAtivo));

        TutorDTO.Response result = tutorService.buscarPorCpf("12345678901");

        assertThat(result.getCpf()).isEqualTo("12345678901");
    }

    @Test
    @DisplayName("criar - deve criar tutor com sucesso quando CPF e email únicos")
    void criar_deveCriarTutorComSucesso() {
        when(tutorRepository.existsByCpf(anyString())).thenReturn(false);
        when(tutorRepository.existsByEmail(anyString())).thenReturn(false);
        when(tutorRepository.save(any(Tutor.class))).thenReturn(tutorAtivo);

        TutorDTO.Response result = tutorService.criar(requestDTO);

        assertThat(result.getNome()).isEqualTo("Maria Silva");
        verify(tutorRepository).save(any(Tutor.class));
    }

    @Test
    @DisplayName("criar - deve lançar BusinessException quando CPF já existe")
    void criar_deveLancarExcecaoQuandoCpfDuplicado() {
        when(tutorRepository.existsByCpf("12345678901")).thenReturn(true);

        assertThatThrownBy(() -> tutorService.criar(requestDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("CPF");

        verify(tutorRepository, never()).save(any());
    }

    @Test
    @DisplayName("criar - deve lançar BusinessException quando email já existe")
    void criar_deveLancarExcecaoQuandoEmailDuplicado() {
        when(tutorRepository.existsByCpf(anyString())).thenReturn(false);
        when(tutorRepository.existsByEmail("maria@email.com")).thenReturn(true);

        assertThatThrownBy(() -> tutorService.criar(requestDTO))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Email");

        verify(tutorRepository, never()).save(any());
    }

    @Test
    @DisplayName("inativar - deve desativar tutor (soft delete)")
    void inativar_deveDesativarTutor() {
        when(tutorRepository.findById(1L)).thenReturn(Optional.of(tutorAtivo));
        when(tutorRepository.save(any(Tutor.class))).thenReturn(tutorAtivo);

        tutorService.inativar(1L);

        assertThat(tutorAtivo.isAtivo()).isFalse();
        verify(tutorRepository).save(tutorAtivo);
    }

    @Test
    @DisplayName("inativar - deve lançar ResourceNotFoundException quando tutor não encontrado")
    void inativar_deveLancarExcecaoQuandoNaoEncontrado() {
        when(tutorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tutorService.inativar(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
