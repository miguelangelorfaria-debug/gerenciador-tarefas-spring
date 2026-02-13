package com.estagio.tarefas.controller;

import com.estagio.tarefas.dto.TarefaRequestDTO;
import com.estagio.tarefas.dto.TarefaResponseDTO;
import com.estagio.tarefas.model.Prioridade;
import com.estagio.tarefas.model.StatusTarefa;
import com.estagio.tarefas.service.TarefaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tarefas")
@RequiredArgsConstructor
@Tag(name = "Tarefas", description = "Endpoints para gerenciamento de tarefas")
public class TarefaController {

    private final TarefaService tarefaService;

    @GetMapping
    @Operation(summary = "Listar todas as tarefas")
    public ResponseEntity<List<TarefaResponseDTO>> listarTodas() {
        List<TarefaResponseDTO> tarefas = tarefaService.listarTodas();
        return ResponseEntity.ok(tarefas);
    }

    @GetMapping("/ordenadas")
    @Operation(summary = "Listar tarefas ordenadas por prioridade e data")
    public ResponseEntity<List<TarefaResponseDTO>> listarOrdenadas() {
        List<TarefaResponseDTO> tarefas = tarefaService.listarOrdenadas();
        return ResponseEntity.ok(tarefas);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar tarefa por ID")
    public ResponseEntity<TarefaResponseDTO> buscarPorId(@PathVariable Long id) {
        TarefaResponseDTO tarefa = tarefaService.buscarPorId(id);
        return ResponseEntity.ok(tarefa);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Buscar tarefas por status")
    public ResponseEntity<List<TarefaResponseDTO>> buscarPorStatus(@PathVariable StatusTarefa status) {
        List<TarefaResponseDTO> tarefas = tarefaService.buscarPorStatus(status);
        return ResponseEntity.ok(tarefas);
    }

    @GetMapping("/prioridade/{prioridade}")
    @Operation(summary = "Buscar tarefas por prioridade")
    public ResponseEntity<List<TarefaResponseDTO>> buscarPorPrioridade(@PathVariable Prioridade prioridade) {
        List<TarefaResponseDTO> tarefas = tarefaService.buscarPorPrioridade(prioridade);
        return ResponseEntity.ok(tarefas);
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar tarefas por termo")
    public ResponseEntity<List<TarefaResponseDTO>> buscarPorTermo(@RequestParam String termo) {
        List<TarefaResponseDTO> tarefas = tarefaService.buscarPorTermo(termo);
        return ResponseEntity.ok(tarefas);
    }

    @PostMapping
    @Operation(summary = "Criar nova tarefa")
    public ResponseEntity<TarefaResponseDTO> criar(@Valid @RequestBody TarefaRequestDTO dto) {
        TarefaResponseDTO tarefa = tarefaService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(tarefa);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar tarefa")
    public ResponseEntity<TarefaResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody TarefaRequestDTO dto) {
        TarefaResponseDTO tarefa = tarefaService.atualizar(id, dto);
        return ResponseEntity.ok(tarefa);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status da tarefa")
    public ResponseEntity<TarefaResponseDTO> atualizarStatus(
            @PathVariable Long id,
            @RequestParam StatusTarefa status) {
        TarefaResponseDTO tarefa = tarefaService.atualizarStatus(id, status);
        return ResponseEntity.ok(tarefa);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar tarefa")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        tarefaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
