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
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;
    private final TutorRepository tutorRepository;
    private final ClinicaRepository clinicaRepository;

    @Cacheable(value = "pets", key = "#pageable.pageNumber")
    public Page<PetDTO.Response> listarAtivos(Pageable pageable) {
        return petRepository.findByAtivoTrue(pageable).map(PetDTO.Response::fromEntity);
    }

    public Page<PetDTO.Response> buscarComFiltros(String nome, Pet.Especie especie, String raca, Pageable pageable) {
        return petRepository.buscarComFiltros(nome, especie, raca, pageable)
                .map(PetDTO.Response::fromEntity);
    }

    @Cacheable(value = "pet", key = "#id")
    public PetDTO.Response buscarPorId(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet", id));
        return PetDTO.Response.fromEntity(pet);
    }

    public List<PetDTO.Response> buscarPorTutor(Long tutorId) {
        return petRepository.findByTutorIdAndAtivoTrue(tutorId)
                .stream().map(PetDTO.Response::fromEntity).toList();
    }

    @Transactional
    @CacheEvict(value = {"pets", "pet"}, allEntries = true)
    public PetDTO.Response criar(PetDTO.Request dto) {
        if (dto.getMicrochip() != null && petRepository.findByMicrochip(dto.getMicrochip()).isPresent()) {
            throw new BusinessException("Microchip " + dto.getMicrochip() + " já cadastrado.");
        }
        Tutor tutor = tutorRepository.findById(dto.getTutorId())
                .orElseThrow(() -> new ResourceNotFoundException("Tutor", dto.getTutorId()));

        Clinica clinica = null;
        if (dto.getClinicaId() != null) {
            clinica = clinicaRepository.findById(dto.getClinicaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Clínica", dto.getClinicaId()));
        }

        Pet pet = Pet.builder()
                .nome(dto.getNome())
                .especie(dto.getEspecie())
                .raca(dto.getRaca())
                .dataNascimento(dto.getDataNascimento())
                .pesoKg(dto.getPesoKg())
                .sexo(dto.getSexo())
                .castrado(dto.getCastrado())
                .microchip(dto.getMicrochip())
                .observacoes(dto.getObservacoes())
                .tutor(tutor)
                .clinicaPrincipal(clinica)
                .ativo(true)
                .build();
        return PetDTO.Response.fromEntity(petRepository.save(pet));
    }

    @Transactional
    @CacheEvict(value = {"pets", "pet"}, allEntries = true)
    public PetDTO.Response atualizar(Long id, PetDTO.Request dto) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet", id));

        if (dto.getMicrochip() != null) {
            petRepository.findByMicrochip(dto.getMicrochip()).ifPresent(p -> {
                if (!p.getId().equals(id)) throw new BusinessException("Microchip já cadastrado em outro pet.");
            });
        }

        Tutor tutor = tutorRepository.findById(dto.getTutorId())
                .orElseThrow(() -> new ResourceNotFoundException("Tutor", dto.getTutorId()));
        Clinica clinica = null;
        if (dto.getClinicaId() != null) {
            clinica = clinicaRepository.findById(dto.getClinicaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Clínica", dto.getClinicaId()));
        }

        pet.setNome(dto.getNome());
        pet.setEspecie(dto.getEspecie());
        pet.setRaca(dto.getRaca());
        pet.setDataNascimento(dto.getDataNascimento());
        pet.setPesoKg(dto.getPesoKg());
        pet.setSexo(dto.getSexo());
        pet.setCastrado(dto.getCastrado());
        pet.setMicrochip(dto.getMicrochip());
        pet.setObservacoes(dto.getObservacoes());
        pet.setTutor(tutor);
        pet.setClinicaPrincipal(clinica);
        return PetDTO.Response.fromEntity(petRepository.save(pet));
    }

    @Transactional
    @CacheEvict(value = {"pets", "pet"}, allEntries = true)
    public void inativar(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet", id));
        pet.setAtivo(false);
        petRepository.save(pet);
    }
}
