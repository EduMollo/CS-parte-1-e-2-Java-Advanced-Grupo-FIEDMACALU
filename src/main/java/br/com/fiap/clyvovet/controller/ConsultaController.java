package br.com.fiap.clyvovet.controller;

import br.com.fiap.clyvovet.dto.ConsultaDTO;
import br.com.fiap.clyvovet.model.Consulta;
import br.com.fiap.clyvovet.model.Consulta.StatusConsulta;
import br.com.fiap.clyvovet.service.ConsultaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/consultas")
@RequiredArgsConstructor
@Tag(name = "Consultas", description = "Gestão de consultas veterinárias")
public class ConsultaController {

    private final ConsultaService consultaService;

    @GetMapping("/pet/{petId}")
    @Operation(summary = "Listar consultas de um pet com paginação")
    public ResponseEntity<Page<ConsultaDTO.Response>> listarPorPet(
            @PathVariable Long petId,
            @PageableDefault(size = 10, sort = "dataConsulta", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(consultaService.listarPorPet(petId, pageable));
    }

    @GetMapping("/pet/{petId}/historico")
    @Operation(summary = "Buscar histórico completo de consultas de um pet (ordenado)")
    public ResponseEntity<List<ConsultaDTO.Response>> historicoCompleto(@PathVariable Long petId) {
        return ResponseEntity.ok(consultaService.buscarHistoricoCompleto(petId));
    }

    @GetMapping("/clinica/{clinicaId}")
    @Operation(summary = "Listar consultas de uma clínica com paginação")
    public ResponseEntity<Page<ConsultaDTO.Response>> listarPorClinica(
            @PathVariable Long clinicaId,
            @PageableDefault(size = 10, sort = "dataConsulta", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(consultaService.listarPorClinica(clinicaId, pageable));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Listar consultas por status")
    public ResponseEntity<Page<ConsultaDTO.Response>> listarPorStatus(
            @PathVariable StatusConsulta status,
            @PageableDefault(size = 10, sort = "dataConsulta", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(consultaService.listarPorStatus(status, pageable));
    }

    @GetMapping("/retornos-pendentes")
    @Operation(summary = "Listar retornos pendentes para hoje ou atrasados")
    public ResponseEntity<List<ConsultaDTO.Response>> retornosPendentes() {
        return ResponseEntity.ok(consultaService.buscarRetornosPendentes());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar consulta por ID")
    public ResponseEntity<ConsultaDTO.Response> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(consultaService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Agendar nova consulta")
    public ResponseEntity<ConsultaDTO.Response> criar(@Valid @RequestBody ConsultaDTO.Request dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(consultaService.criar(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar dados da consulta")
    public ResponseEntity<ConsultaDTO.Response> atualizar(
            @PathVariable Long id, @Valid @RequestBody ConsultaDTO.Request dto) {
        return ResponseEntity.ok(consultaService.atualizar(id, dto));
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar uma consulta")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        consultaService.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}
