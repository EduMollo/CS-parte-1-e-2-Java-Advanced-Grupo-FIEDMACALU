package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.MedicamentoDTO;
import br.com.fiap.clyvovet.exception.ResourceNotFoundException;
import br.com.fiap.clyvovet.model.Consulta;
import br.com.fiap.clyvovet.model.Medicamento;
import br.com.fiap.clyvovet.model.Pet;
import br.com.fiap.clyvovet.repository.ConsultaRepository;
import br.com.fiap.clyvovet.repository.MedicamentoRepository;
import br.com.fiap.clyvovet.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicamentoService {

    private final MedicamentoRepository medicamentoRepository;
    private final PetRepository petRepository;
    private final ConsultaRepository consultaRepository;

    public Page<MedicamentoDTO.Response> listarPorPet(Long petId, Pageable pageable) {
        return medicamentoRepository.findByPetId(petId, pageable).map(MedicamentoDTO.Response::fromEntity);
    }

    public List<MedicamentoDTO.Response> buscarMedicamentosAtivos(Long petId) {
        return medicamentoRepository.findMedicamentosAtivos(petId)
                .stream().map(MedicamentoDTO.Response::fromEntity).toList();
    }

    public List<MedicamentoDTO.Response> buscarMedicamentosContinuosAtivos(Long petId) {
        return medicamentoRepository.findMedicamentosContiuosAtivos(petId)
                .stream().map(MedicamentoDTO.Response::fromEntity).toList();
    }

    @Transactional
    public MedicamentoDTO.Response registrar(MedicamentoDTO.Request dto) {
        Pet pet = petRepository.findById(dto.getPetId())
                .orElseThrow(() -> new ResourceNotFoundException("Pet", dto.getPetId()));

        Consulta consulta = null;
        if (dto.getConsultaId() != null) {
            consulta = consultaRepository.findById(dto.getConsultaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Consulta", dto.getConsultaId()));
        }

        Medicamento med = Medicamento.builder()
                .nome(dto.getNome())
                .principioAtivo(dto.getPrincipioAtivo())
                .dosagem(dto.getDosagem())
                .frequencia(dto.getFrequencia())
                .dataInicio(dto.getDataInicio())
                .dataFim(dto.getDataFim())
                .usoContinuo(dto.isUsoContinuo())
                .observacoes(dto.getObservacoes())
                .pet(pet)
                .consultaOrigem(consulta)
                .build();
        return MedicamentoDTO.Response.fromEntity(medicamentoRepository.save(med));
    }
}
