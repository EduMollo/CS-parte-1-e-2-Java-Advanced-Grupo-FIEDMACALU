package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.ClinicaDTO;
import br.com.fiap.clyvovet.exception.BusinessException;
import br.com.fiap.clyvovet.exception.ResourceNotFoundException;
import br.com.fiap.clyvovet.model.Clinica;
import br.com.fiap.clyvovet.repository.ClinicaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClinicaService {

    private final ClinicaRepository clinicaRepository;

    @Cacheable(value = "clinicas", key = "#pageable.pageNumber")
    public Page<ClinicaDTO.Response> listarAtivas(Pageable pageable) {
        return clinicaRepository.findByAtivoTrue(pageable).map(ClinicaDTO.Response::fromEntity);
    }

    public Page<ClinicaDTO.Response> buscarComFiltros(String nome, String cidade, String estado, Pageable pageable) {
        return clinicaRepository.buscarComFiltros(nome, cidade, estado, pageable)
                .map(ClinicaDTO.Response::fromEntity);
    }

    @Cacheable(value = "clinica", key = "#id")
    public ClinicaDTO.Response buscarPorId(Long id) {
        Clinica clinica = clinicaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clínica", id));
        return ClinicaDTO.Response.fromEntity(clinica);
    }

    @Transactional
    @CacheEvict(value = {"clinicas", "clinica"}, allEntries = true)
    public ClinicaDTO.Response criar(ClinicaDTO.Request dto) {
        if (clinicaRepository.existsByCnpj(dto.getCnpj())) {
            throw new BusinessException("CNPJ " + dto.getCnpj() + " já cadastrado.");
        }
        Clinica clinica = Clinica.builder()
                .nome(dto.getNome())
                .cnpj(dto.getCnpj())
                .telefone(dto.getTelefone())
                .email(dto.getEmail())
                .endereco(dto.getEndereco())
                .cidade(dto.getCidade())
                .estado(dto.getEstado())
                .ativo(true)
                .build();
        return ClinicaDTO.Response.fromEntity(clinicaRepository.save(clinica));
    }

    @Transactional
    @CacheEvict(value = {"clinicas", "clinica"}, allEntries = true)
    public ClinicaDTO.Response atualizar(Long id, ClinicaDTO.Request dto) {
        Clinica clinica = clinicaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clínica", id));

        clinicaRepository.findByCnpj(dto.getCnpj()).ifPresent(c -> {
            if (!c.getId().equals(id)) throw new BusinessException("CNPJ já utilizado por outra clínica.");
        });

        clinica.setNome(dto.getNome());
        clinica.setCnpj(dto.getCnpj());
        clinica.setTelefone(dto.getTelefone());
        clinica.setEmail(dto.getEmail());
        clinica.setEndereco(dto.getEndereco());
        clinica.setCidade(dto.getCidade());
        clinica.setEstado(dto.getEstado());
        return ClinicaDTO.Response.fromEntity(clinicaRepository.save(clinica));
    }

    @Transactional
    @CacheEvict(value = {"clinicas", "clinica"}, allEntries = true)
    public void inativar(Long id) {
        Clinica clinica = clinicaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clínica", id));
        clinica.setAtivo(false);
        clinicaRepository.save(clinica);
    }
}
