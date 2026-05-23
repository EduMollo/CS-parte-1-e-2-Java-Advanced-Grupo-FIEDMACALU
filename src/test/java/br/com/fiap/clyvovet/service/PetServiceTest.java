package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.PetDTO;
import br.com.fiap.clyvovet.exception.BusinessException;
import br.com.fiap.clyvovet.exception.ResourceNotFoundException;
import br.com.fiap.clyvovet.model.Clinica;
import br.com.fiap.clyvovet.model.Pet;
import br.com.fiap.clyvovet.model.Tutor;
import br.com.fiap.clyvovet.repository.ClinicaRepository;
import br.com.fiap.clyvovet.repository.PetRepository;
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
@DisplayName("PetService - Testes Unitários")
class PetServiceTest {

    @Mock
    private PetRepository petRepository;
    @Mock
    private TutorRepository tutorRepository;
    @Mock
    private ClinicaRepository clinicaRepository;

    @InjectMocks
    private PetService petService;

    private Pet petAtivo;
    private Tutor tutor;
    private Clinica clinica;
    private PetDTO.Request requestDTO;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        tutor = Tutor.builder()
                .id(1L).nome("Maria Silva").cpf("12345678901")
                .email("maria@email.com").telefone("11987654321")
                .dataNascimento(LocalDate.of(1990, 5, 15)).ativo(true).build();

        clinica = Clinica.builder()
                .id(1L).nome("Clínica VetLife").cnpj("12345678000100")
                .telefone("1134567890").cidade("São Paulo").estado("SP").ativo(true).build();

        petAtivo = Pet.builder()
                .id(1L).nome("Rex").especie(Pet.Especie.CACHORRO).raca("Labrador")
                .dataNascimento(LocalDate.of(2020, 3, 10)).pesoKg(25.0)
                .sexo(Pet.Sexo.MACHO).castrado(false).ativo(true)
                .tutor(tutor).clinicaPrincipal(clinica).build();

        requestDTO = PetDTO.Request.builder()
                .nome("Rex").especie(Pet.Especie.CACHORRO).raca("Labrador")
                .dataNascimento(LocalDate.of(2020, 3, 10)).pesoKg(25.0)
                .sexo(Pet.Sexo.MACHO).castrado(false)
                .tutorId(1L).clinicaId(1L).build();

        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("listarAtivos - deve retornar página de pets ativos")
    void listarAtivos_deveRetornarPaginaDePets() {
        Page<Pet> page = new PageImpl<>(List.of(petAtivo));
        when(petRepository.findByAtivoTrue(pageable)).thenReturn(page);

        Page<PetDTO.Response> result = petService.listarAtivos(pageable);

        assertThat(result).isNotEmpty();
        assertThat(result.getContent().get(0).getNome()).isEqualTo("Rex");
        verify(petRepository).findByAtivoTrue(pageable);
    }

    @Test
    @DisplayName("buscarPorId - deve retornar pet quando ID existe")
    void buscarPorId_deveRetornarPetExistente() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(petAtivo));

        PetDTO.Response result = petService.buscarPorId(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNome()).isEqualTo("Rex");
        assertThat(result.getEspecie()).isEqualTo(Pet.Especie.CACHORRO);
    }

    @Test
    @DisplayName("buscarPorId - deve lançar exceção quando pet não encontrado")
    void buscarPorId_deveLancarExcecaoQuandoNaoEncontrado() {
        when(petRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> petService.buscarPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("criar - deve criar pet com sucesso")
    void criar_deveCriarPetComSucesso() {
        when(tutorRepository.findById(1L)).thenReturn(Optional.of(tutor));
        when(clinicaRepository.findById(1L)).thenReturn(Optional.of(clinica));
        when(petRepository.save(any(Pet.class))).thenReturn(petAtivo);

        PetDTO.Response result = petService.criar(requestDTO);

        assertThat(result.getNome()).isEqualTo("Rex");
        verify(petRepository).save(any(Pet.class));
    }

    @Test
    @DisplayName("criar - deve lançar exceção quando microchip duplicado")
    void criar_deveLancarExcecaoQuandoMicrochipDuplicado() {
        PetDTO.Request dtoComMicrochip = PetDTO.Request.builder()
                .nome("Rex").especie(Pet.Especie.CACHORRO).raca("Labrador")
                .dataNascimento(LocalDate.of(2020, 3, 10)).pesoKg(25.0)
                .microchip("CHIP001").tutorId(1L).build();

        when(petRepository.findByMicrochip("CHIP001")).thenReturn(Optional.of(petAtivo));

        assertThatThrownBy(() -> petService.criar(dtoComMicrochip))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Microchip");

        verify(petRepository, never()).save(any());
    }

    @Test
    @DisplayName("criar - deve lançar exceção quando tutor não encontrado")
    void criar_deveLancarExcecaoQuandoTutorNaoEncontrado() {
        when(tutorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> petService.criar(requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Tutor");
    }

    @Test
    @DisplayName("buscarPorTutor - deve retornar lista de pets do tutor")
    void buscarPorTutor_deveRetornarPetsDoTutor() {
        when(petRepository.findByTutorIdAndAtivoTrue(1L)).thenReturn(List.of(petAtivo));

        List<PetDTO.Response> result = petService.buscarPorTutor(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNome()).isEqualTo("Rex");
    }

    @Test
    @DisplayName("inativar - deve desativar pet (soft delete)")
    void inativar_deveDesativarPet() {
        when(petRepository.findById(1L)).thenReturn(Optional.of(petAtivo));
        when(petRepository.save(any(Pet.class))).thenReturn(petAtivo);

        petService.inativar(1L);

        assertThat(petAtivo.isAtivo()).isFalse();
        verify(petRepository).save(petAtivo);
    }
}
