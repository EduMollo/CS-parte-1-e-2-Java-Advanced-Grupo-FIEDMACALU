package br.com.fiap.clyvovet.controller;

import br.com.fiap.clyvovet.dto.EventoSaudeDTO;
import br.com.fiap.clyvovet.service.EventoSaudeService;
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
@RequestMapping("/api/v1/eventos-saude")
@RequiredArgsConstructor
@Tag(name = "Eventos de Saúde", description = "Timeline de saúde e alertas proativos do pet")
public class EventoSaudeController {

    private final EventoSaudeService eventoSaudeService;

    @GetMapping("/pet/{petId}")
    @Operation(summary = "Listar eventos de saúde de um pet com paginação")
    public ResponseEntity<Page<EventoSaudeDTO.Response>> listarPorPet(
            @PathVariable Long petId,
            @PageableDefault(size = 10, sort = "dataEvento", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(eventoSaudeService.listarPorPet(petId, pageable));
    }

    @GetMapping("/pet/{petId}/timeline")
    @Operation(summary = "Buscar timeline completa de saúde do pet")
    public ResponseEntity<List<EventoSaudeDTO.Response>> timeline(@PathVariable Long petId) {
        return ResponseEntity.ok(eventoSaudeService.buscarTimeline(petId));
    }

    @GetMapping("/pet/{petId}/alertas")
    @Operation(summary = "Buscar alertas pendentes (próxima ação não concluída)")
    public ResponseEntity<List<EventoSaudeDTO.Response>> alertasPorPet(@PathVariable Long petId) {
        return ResponseEntity.ok(eventoSaudeService.buscarAlertasPendentes(petId));
    }

    @GetMapping("/tutor/{tutorId}/alertas")
    @Operation(summary = "Buscar alertas pendentes de todos os pets de um tutor")
    public ResponseEntity<List<EventoSaudeDTO.Response>> alertasPorTutor(@PathVariable Long tutorId) {
        return ResponseEntity.ok(eventoSaudeService.buscarAlertasByTutor(tutorId));
    }

    @PostMapping
    @Operation(summary = "Registrar novo evento de saúde")
    public ResponseEntity<EventoSaudeDTO.Response> criar(@Valid @RequestBody EventoSaudeDTO.Request dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventoSaudeService.criar(dto));
    }

    @PatchMapping("/{id}/concluir")
    @Operation(summary = "Marcar evento como concluído")
    public ResponseEntity<EventoSaudeDTO.Response> concluir(@PathVariable Long id) {
        return ResponseEntity.ok(eventoSaudeService.concluir(id));
    }
}
