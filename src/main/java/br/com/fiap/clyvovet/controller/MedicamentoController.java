package br.com.fiap.clyvovet.controller;

import br.com.fiap.clyvovet.dto.MedicamentoDTO;
import br.com.fiap.clyvovet.service.MedicamentoService;
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
@RequestMapping("/api/v1/medicamentos")
@RequiredArgsConstructor
@Tag(name = "Medicamentos", description = "Gestão de medicamentos e prescrições do pet")
public class MedicamentoController {

    private final MedicamentoService medicamentoService;

    @GetMapping("/pet/{petId}")
    @Operation(summary = "Listar medicamentos de um pet com paginação")
    public ResponseEntity<Page<MedicamentoDTO.Response>> listarPorPet(
            @PathVariable Long petId,
            @PageableDefault(size = 10, sort = "dataInicio", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(medicamentoService.listarPorPet(petId, pageable));
    }

    @GetMapping("/pet/{petId}/ativos")
    @Operation(summary = "Listar medicamentos ativos no período atual")
    public ResponseEntity<List<MedicamentoDTO.Response>> ativos(@PathVariable Long petId) {
        return ResponseEntity.ok(medicamentoService.buscarMedicamentosAtivos(petId));
    }

    @GetMapping("/pet/{petId}/uso-continuo")
    @Operation(summary = "Listar medicamentos de uso contínuo ativos")
    public ResponseEntity<List<MedicamentoDTO.Response>> usoContinuo(@PathVariable Long petId) {
        return ResponseEntity.ok(medicamentoService.buscarMedicamentosContinuosAtivos(petId));
    }

    @PostMapping
    @Operation(summary = "Registrar novo medicamento/prescrição")
    public ResponseEntity<MedicamentoDTO.Response> registrar(@Valid @RequestBody MedicamentoDTO.Request dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(medicamentoService.registrar(dto));
    }
}
