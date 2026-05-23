package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.TutorDTO;
import br.com.fiap.clyvovet.exception.BusinessException;
import br.com.fiap.clyvovet.exception.ResourceNotFoundException;
import br.com.fiap.clyvovet.model.Tutor;
import br.com.fiap.clyvovet.repository.TutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TutorService {

    private final TutorRepository tutorRepository;

    @Cacheable(value = "tutores", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<TutorDTO.Response> listarAtivos(Pageable pageable) {
        return tutorRepository.findByAtivoTrue(pageable)
                .map(TutorDTO.Response::fromEntity);
    }

    public Page<TutorDTO.Response> buscarComFiltros(String nome, String email, Pageable pageable) {
        return tutorRepository.buscarComFiltros(nome, email, pageable)
                .map(TutorDTO.Response::fromEntity);
    }

    @Cacheable(value = "tutor", key = "#id")
    public TutorDTO.Response buscarPorId(Long id) {
        Tutor tutor = tutorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor", id));
        return TutorDTO.Response.fromEntity(tutor);
    }

    public TutorDTO.Response buscarPorCpf(String cpf) {
        Tutor tutor = tutorRepository.findByCpf(cpf)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor com CPF " + cpf + " não encontrado"));
        return TutorDTO.Response.fromEntity(tutor);
    }

    @Transactional
    @CacheEvict(value = {"tutores", "tutor"}, allEntries = true)
    public TutorDTO.Response criar(TutorDTO.Request dto) {
        if (tutorRepository.existsByCpf(dto.getCpf())) {
            throw new BusinessException("CPF " + dto.getCpf() + " já cadastrado.");
        }
        if (tutorRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Email " + dto.getEmail() + " já cadastrado.");
        }
        Tutor tutor = Tutor.builder()
                .nome(dto.getNome())
                .cpf(dto.getCpf())
                .email(dto.getEmail())
                .telefone(dto.getTelefone())
                .dataNascimento(dto.getDataNascimento())
                .ativo(true)
                .build();
        return TutorDTO.Response.fromEntity(tutorRepository.save(tutor));
    }

    @Transactional
    @CacheEvict(value = {"tutores", "tutor"}, allEntries = true)
    public TutorDTO.Response atualizar(Long id, TutorDTO.Request dto) {
        Tutor tutor = tutorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor", id));

        tutorRepository.findByCpf(dto.getCpf()).ifPresent(t -> {
            if (!t.getId().equals(id)) throw new BusinessException("CPF já utilizado por outro tutor.");
        });
        tutorRepository.findByEmail(dto.getEmail()).ifPresent(t -> {
            if (!t.getId().equals(id)) throw new BusinessException("Email já utilizado por outro tutor.");
        });

        tutor.setNome(dto.getNome());
        tutor.setCpf(dto.getCpf());
        tutor.setEmail(dto.getEmail());
        tutor.setTelefone(dto.getTelefone());
        tutor.setDataNascimento(dto.getDataNascimento());
        return TutorDTO.Response.fromEntity(tutorRepository.save(tutor));
    }

    @Transactional
    @CacheEvict(value = {"tutores", "tutor"}, allEntries = true)
    public void inativar(Long id) {
        Tutor tutor = tutorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tutor", id));
        tutor.setAtivo(false);
        tutorRepository.save(tutor);
    }
}
