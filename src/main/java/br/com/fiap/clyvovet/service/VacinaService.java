package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.VacinaDTO;
import br.com.fiap.clyvovet.exception.ResourceNotFoundException;
import br.com.fiap.clyvovet.model.Clinica;
import br.com.fiap.clyvovet.model.Pet;
import br.com.fiap.clyvovet.model.Vacina;
import br.com.fiap.clyvovet.repository.ClinicaRepository;
import br.com.fiap.clyvovet.repository.PetRepository;
import br.com.fiap.clyvovet.repository.VacinaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VacinaService {

    private final VacinaRepository vacinaRepository;
    private final PetRepository petRepository;
    private final ClinicaRepository clinicaRepository;

    public Page<VacinaDTO.Response> listarPorPet(Long petId, Pageable pageable) {
        return vacinaRepository.findByPetId(petId, pageable).map(VacinaDTO.Response::fromEntity);
    }

    public List<VacinaDTO.Response> buscarVacinasPendentes(Long petId) {
        return vacinaRepository.findVacinasPendentes(petId, LocalDate.now())
                .stream().map(VacinaDTO.Response::fromEntity).toList();
    }

    public List<VacinaDTO.Response> buscarVacinasPendentesByTutor(Long tutorId) {
        return vacinaRepository.findVacinasPendentesByTutor(tutorId, LocalDate.now())
                .stream().map(VacinaDTO.Response::fromEntity).toList();
    }

    @Transactional
    public VacinaDTO.Response registrar(VacinaDTO.Request dto) {
        Pet pet = petRepository.findById(dto.getPetId())
                .orElseThrow(() -> new ResourceNotFoundException("Pet", dto.getPetId()));

        Clinica clinica = null;
        if (dto.getClinicaId() != null) {
            clinica = clinicaRepository.findById(dto.getClinicaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Clínica", dto.getClinicaId()));
        }

        Vacina vacina = Vacina.builder()
                .nome(dto.getNome())
                .fabricante(dto.getFabricante())
                .lote(dto.getLote())
                .dataAplicacao(dto.getDataAplicacao())
                .dataValidade(dto.getDataValidade())
                .proximaDose(dto.getProximaDose())
                .pet(pet)
                .clinica(clinica)
                .build();
        return VacinaDTO.Response.fromEntity(vacinaRepository.save(vacina));
    }
}
