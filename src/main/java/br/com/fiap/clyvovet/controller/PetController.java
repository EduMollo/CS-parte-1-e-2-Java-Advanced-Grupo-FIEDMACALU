package br.com.fiap.clyvovet.controller;

import br.com.fiap.clyvovet.dto.PetDTO;
import br.com.fiap.clyvovet.dto.SaudePetDashboardDTO;
import br.com.fiap.clyvovet.model.Pet;
import br.com.fiap.clyvovet.service.PetService;
import br.com.fiap.clyvovet.service.SaudePetService;
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
@RequestMapping("/api/v1/pets")
@RequiredArgsConstructor
@Tag(name = "Pets", description = "Gestão de pets cadastrados na plataforma")
public class PetController {

    private final PetService petService;
    private final SaudePetService saudePetService;

    @GetMapping
    @Operation(summary = "Listar pets ativos com filtros, paginação e ordenação")
    public ResponseEntity<Page<PetDTO.Response>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Pet.Especie especie,
            @RequestParam(required = false) String raca,
            @PageableDefault(size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {

        if (nome != null || especie != null || raca != null) {
            return ResponseEntity.ok(petService.buscarComFiltros(nome, especie, raca, pageable));
        }
        return ResponseEntity.ok(petService.listarAtivos(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pet por ID")
    public ResponseEntity<PetDTO.Response> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(petService.buscarPorId(id));
    }

    @GetMapping("/tutor/{tutorId}")
    @Operation(summary = "Listar todos os pets ativos de um tutor")
    public ResponseEntity<List<PetDTO.Response>> buscarPorTutor(@PathVariable Long tutorId) {
        return ResponseEntity.ok(petService.buscarPorTutor(tutorId));
    }

    @GetMapping("/{id}/dashboard")
    @Operation(summary = "Dashboard de saúde do pet com score de risco e alertas proativos")
    public ResponseEntity<SaudePetDashboardDTO> dashboard(@PathVariable Long id) {
        return ResponseEntity.ok(saudePetService.gerarDashboard(id));
    }

    @PostMapping
    @Operation(summary = "Cadastrar novo pet")
    public ResponseEntity<PetDTO.Response> criar(@Valid @RequestBody PetDTO.Request dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(petService.criar(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar dados do pet")
    public ResponseEntity<PetDTO.Response> atualizar(
            @PathVariable Long id, @Valid @RequestBody PetDTO.Request dto) {
        return ResponseEntity.ok(petService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Inativar pet (soft delete)")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        petService.inativar(id);
        return ResponseEntity.noContent().build();
    }
}
