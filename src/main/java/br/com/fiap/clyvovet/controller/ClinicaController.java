package br.com.fiap.clyvovet.controller;

import br.com.fiap.clyvovet.dto.ClinicaDTO;
import br.com.fiap.clyvovet.service.ClinicaService;
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

@RestController
@RequestMapping("/api/v1/clinicas")
@RequiredArgsConstructor
@Tag(name = "Clínicas", description = "Gestão de clínicas veterinárias parceiras")
public class ClinicaController {

    private final ClinicaService clinicaService;

    @GetMapping
    @Operation(summary = "Listar clínicas ativas com filtros, paginação e ordenação")
    public ResponseEntity<Page<ClinicaDTO.Response>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cidade,
            @RequestParam(required = false) String estado,
            @PageableDefault(size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {

        if (nome != null || cidade != null || estado != null) {
            return ResponseEntity.ok(clinicaService.buscarComFiltros(nome, cidade, estado, pageable));
        }
        return ResponseEntity.ok(clinicaService.listarAtivas(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar clínica por ID")
    public ResponseEntity<ClinicaDTO.Response> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(clinicaService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Cadastrar nova clínica")
    public ResponseEntity<ClinicaDTO.Response> criar(@Valid @RequestBody ClinicaDTO.Request dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clinicaService.criar(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar dados da clínica")
    public ResponseEntity<ClinicaDTO.Response> atualizar(
            @PathVariable Long id, @Valid @RequestBody ClinicaDTO.Request dto) {
        return ResponseEntity.ok(clinicaService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Inativar clínica (soft delete)")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        clinicaService.inativar(id);
        return ResponseEntity.noContent().build();
    }
}
