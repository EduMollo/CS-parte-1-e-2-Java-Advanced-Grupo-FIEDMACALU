package br.com.fiap.clyvovet.controller;

import br.com.fiap.clyvovet.dto.VacinaDTO;
import br.com.fiap.clyvovet.service.VacinaService;
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
@RequestMapping("/api/v1/vacinas")
@RequiredArgsConstructor
@Tag(name = "Vacinas", description = "Carteira de vacinação digital do pet")
public class VacinaController {

    private final VacinaService vacinaService;

    @GetMapping("/pet/{petId}")
    @Operation(summary = "Listar vacinas de um pet com paginação")
    public ResponseEntity<Page<VacinaDTO.Response>> listarPorPet(
            @PathVariable Long petId,
            @PageableDefault(size = 10, sort = "dataAplicacao", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(vacinaService.listarPorPet(petId, pageable));
    }

    @GetMapping("/pet/{petId}/pendentes")
    @Operation(summary = "Listar vacinas pendentes (próxima dose ou validade vencida)")
    public ResponseEntity<List<VacinaDTO.Response>> pendentes(@PathVariable Long petId) {
        return ResponseEntity.ok(vacinaService.buscarVacinasPendentes(petId));
    }

    @GetMapping("/tutor/{tutorId}/pendentes")
    @Operation(summary = "Listar todas vacinas pendentes dos pets de um tutor")
    public ResponseEntity<List<VacinaDTO.Response>> pendentesPorTutor(@PathVariable Long tutorId) {
        return ResponseEntity.ok(vacinaService.buscarVacinasPendentesByTutor(tutorId));
    }

    @PostMapping
    @Operation(summary = "Registrar nova vacina")
    public ResponseEntity<VacinaDTO.Response> registrar(@Valid @RequestBody VacinaDTO.Request dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vacinaService.registrar(dto));
    }
}
