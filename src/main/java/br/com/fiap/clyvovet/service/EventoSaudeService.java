package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.EventoSaudeDTO;
import br.com.fiap.clyvovet.exception.ResourceNotFoundException;
import br.com.fiap.clyvovet.model.Consulta;
import br.com.fiap.clyvovet.model.EventoSaude;
import br.com.fiap.clyvovet.model.Pet;
import br.com.fiap.clyvovet.repository.ConsultaRepository;
import br.com.fiap.clyvovet.repository.EventoSaudeRepository;
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
public class EventoSaudeService {

    private final EventoSaudeRepository eventoSaudeRepository;
    private final PetRepository petRepository;
    private final ConsultaRepository consultaRepository;

    public Page<EventoSaudeDTO.Response> listarPorPet(Long petId, Pageable pageable) {
        return eventoSaudeRepository.findByPetId(petId, pageable).map(EventoSaudeDTO.Response::fromEntity);
    }

    public List<EventoSaudeDTO.Response> buscarTimeline(Long petId) {
        return eventoSaudeRepository.findTimelinePet(petId)
                .stream().map(EventoSaudeDTO.Response::fromEntity).toList();
    }

    public List<EventoSaudeDTO.Response> buscarAlertasPendentes(Long petId) {
        return eventoSaudeRepository.findAlertasPendentes(petId, LocalDate.now())
                .stream().map(EventoSaudeDTO.Response::fromEntity).toList();
    }

    public List<EventoSaudeDTO.Response> buscarAlertasByTutor(Long tutorId) {
        return eventoSaudeRepository.findAlertasByTutor(tutorId, LocalDate.now())
                .stream().map(EventoSaudeDTO.Response::fromEntity).toList();
    }

    @Transactional
    public EventoSaudeDTO.Response criar(EventoSaudeDTO.Request dto) {
        Pet pet = petRepository.findById(dto.getPetId())
                .orElseThrow(() -> new ResourceNotFoundException("Pet", dto.getPetId()));

        Consulta consulta = null;
        if (dto.getConsultaId() != null) {
            consulta = consultaRepository.findById(dto.getConsultaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Consulta", dto.getConsultaId()));
        }

        EventoSaude evento = EventoSaude.builder()
                .tipoEvento(dto.getTipoEvento())
                .descricao(dto.getDescricao())
                .dataEvento(dto.getDataEvento())
                .dataProximaAcao(dto.getDataProximaAcao())
                .concluido(false)
                .pet(pet)
                .consulta(consulta)
                .build();
        return EventoSaudeDTO.Response.fromEntity(eventoSaudeRepository.save(evento));
    }

    @Transactional
    public EventoSaudeDTO.Response concluir(Long id) {
        EventoSaude evento = eventoSaudeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento de Saúde", id));
        evento.setConcluido(true);
        return EventoSaudeDTO.Response.fromEntity(eventoSaudeRepository.save(evento));
    }
}
