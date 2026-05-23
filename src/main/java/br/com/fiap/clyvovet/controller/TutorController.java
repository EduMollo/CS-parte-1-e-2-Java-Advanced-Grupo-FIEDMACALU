package br.com.fiap.clyvovet.controller;

import br.com.fiap.clyvovet.dto.TutorDTO;
import br.com.fiap.clyvovet.service.TutorService;
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
@RequestMapping("/api/v1/tutores")
@RequiredArgsConstructor
@Tag(name = "Tutores", description = "Gestão de tutores (donos de pets)")
public class TutorController {

    private final TutorService tutorService;

    @GetMapping
    @Operation(summary = "Listar tutores ativos com paginação, ordenação e filtros")
    public ResponseEntity<Page<TutorDTO.Response>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String email,
            @PageableDefault(size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {

        if (nome != null || email != null) {
            return ResponseEntity.ok(tutorService.buscarComFiltros(nome, email, pageable));
        }
        return ResponseEntity.ok(tutorService.listarAtivos(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar tutor por ID")
    public ResponseEntity<TutorDTO.Response> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(tutorService.buscarPorId(id));
    }

    @GetMapping("/cpf/{cpf}")
    @Operation(summary = "Buscar tutor por CPF")
    public ResponseEntity<TutorDTO.Response> buscarPorCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(tutorService.buscarPorCpf(cpf));
    }

    @PostMapping
    @Operation(summary = "Cadastrar novo tutor")
    public ResponseEntity<TutorDTO.Response> criar(@Valid @RequestBody TutorDTO.Request dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tutorService.criar(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar dados do tutor")
    public ResponseEntity<TutorDTO.Response> atualizar(
            @PathVariable Long id, @Valid @RequestBody TutorDTO.Request dto) {
        return ResponseEntity.ok(tutorService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Inativar tutor (soft delete)")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        tutorService.inativar(id);
        return ResponseEntity.noContent().build();
    }
}
