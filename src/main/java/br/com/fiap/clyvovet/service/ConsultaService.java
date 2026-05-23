package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.ConsultaDTO;
import br.com.fiap.clyvovet.exception.BusinessException;
import br.com.fiap.clyvovet.exception.ResourceNotFoundException;
import br.com.fiap.clyvovet.model.Clinica;
import br.com.fiap.clyvovet.model.Consulta;
import br.com.fiap.clyvovet.model.Consulta.StatusConsulta;
import br.com.fiap.clyvovet.model.Pet;
import br.com.fiap.clyvovet.repository.ClinicaRepository;
import br.com.fiap.clyvovet.repository.ConsultaRepository;
import br.com.fiap.clyvovet.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final PetRepository petRepository;
    private final ClinicaRepository clinicaRepository;

    public Page<ConsultaDTO.Response> listarPorPet(Long petId, Pageable pageable) {
        return consultaRepository.findByPetId(petId, pageable).map(ConsultaDTO.Response::fromEntity);
    }

    public Page<ConsultaDTO.Response> listarPorClinica(Long clinicaId, Pageable pageable) {
        return consultaRepository.findByClinicaId(clinicaId, pageable).map(ConsultaDTO.Response::fromEntity);
    }

    public Page<ConsultaDTO.Response> listarPorStatus(StatusConsulta status, Pageable pageable) {
        return consultaRepository.findByStatus(status, pageable).map(ConsultaDTO.Response::fromEntity);
    }

    public ConsultaDTO.Response buscarPorId(Long id) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta", id));
        return ConsultaDTO.Response.fromEntity(consulta);
    }

    public List<ConsultaDTO.Response> buscarHistoricoCompleto(Long petId) {
        return consultaRepository.findHistoricoCompleto(petId)
                .stream().map(ConsultaDTO.Response::fromEntity).toList();
    }

    public List<ConsultaDTO.Response> buscarRetornosPendentes() {
        return consultaRepository.findRetornosPendentes(LocalDate.now().minusDays(30), LocalDate.now().plusDays(30))
                .stream().map(ConsultaDTO.Response::fromEntity).toList();
    }

    @Transactional
    public ConsultaDTO.Response criar(ConsultaDTO.Request dto) {
        Pet pet = petRepository.findById(dto.getPetId())
                .orElseThrow(() -> new ResourceNotFoundException("Pet", dto.getPetId()));

        Clinica clinica = null;
        if (dto.getClinicaId() != null) {
            clinica = clinicaRepository.findById(dto.getClinicaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Clínica", dto.getClinicaId()));
        }

        Consulta consulta = Consulta.builder()
                .dataConsulta(dto.getDataConsulta())
                .status(dto.getStatus())
                .tipoConsulta(dto.getTipoConsulta())
                .veterinario(dto.getVeterinario())
                .diagnostico(dto.getDiagnostico())
                .prescricao(dto.getPrescricao())
                .observacoes(dto.getObservacoes())
                .valor(dto.getValor())
                .dataRetorno(dto.getDataRetorno())
                .pet(pet)
                .clinica(clinica)
                .build();
        return ConsultaDTO.Response.fromEntity(consultaRepository.save(consulta));
    }

    @Transactional
    public ConsultaDTO.Response atualizar(Long id, ConsultaDTO.Request dto) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta", id));

        if (consulta.getStatus() == StatusConsulta.CANCELADA) {
            throw new BusinessException("Não é possível atualizar uma consulta cancelada.");
        }

        Pet pet = petRepository.findById(dto.getPetId())
                .orElseThrow(() -> new ResourceNotFoundException("Pet", dto.getPetId()));
        Clinica clinica = null;
        if (dto.getClinicaId() != null) {
            clinica = clinicaRepository.findById(dto.getClinicaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Clínica", dto.getClinicaId()));
        }

        consulta.setDataConsulta(dto.getDataConsulta());
        consulta.setStatus(dto.getStatus());
        consulta.setTipoConsulta(dto.getTipoConsulta());
        consulta.setVeterinario(dto.getVeterinario());
        consulta.setDiagnostico(dto.getDiagnostico());
        consulta.setPrescricao(dto.getPrescricao());
        consulta.setObservacoes(dto.getObservacoes());
        consulta.setValor(dto.getValor());
        consulta.setDataRetorno(dto.getDataRetorno());
        consulta.setPet(pet);
        consulta.setClinica(clinica);
        return ConsultaDTO.Response.fromEntity(consultaRepository.save(consulta));
    }

    @Transactional
    public void cancelar(Long id) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta", id));
        if (consulta.getStatus() == StatusConsulta.REALIZADA) {
            throw new BusinessException("Não é possível cancelar uma consulta já realizada.");
        }
        consulta.setStatus(StatusConsulta.CANCELADA);
        consultaRepository.save(consulta);
    }
}
