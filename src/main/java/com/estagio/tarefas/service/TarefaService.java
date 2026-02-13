package com.estagio.tarefas.service;

import com.estagio.tarefas.dto.TarefaRequestDTO;
import com.estagio.tarefas.dto.TarefaResponseDTO;
import com.estagio.tarefas.exception.BusinessException;
import com.estagio.tarefas.exception.ResourceNotFoundException;
import com.estagio.tarefas.model.Prioridade;
import com.estagio.tarefas.model.StatusTarefa;
import com.estagio.tarefas.model.Tarefa;
import com.estagio.tarefas.repository.TarefaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TarefaService {

    private final TarefaRepository tarefaRepository;

    @Transactional(readOnly = true)
    public List<TarefaResponseDTO> listarTodas() {
        return tarefaRepository.findAll()
                .stream()
                .map(TarefaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TarefaResponseDTO buscarPorId(Long id) {
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada com ID: " + id));
        return TarefaResponseDTO.fromEntity(tarefa);
    }

    @Transactional(readOnly = true)
    public List<TarefaResponseDTO> buscarPorStatus(StatusTarefa status) {
        return tarefaRepository.findByStatus(status)
                .stream()
                .map(TarefaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TarefaResponseDTO> buscarPorPrioridade(Prioridade prioridade) {
        return tarefaRepository.findByPrioridade(prioridade)
                .stream()
                .map(TarefaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TarefaResponseDTO> buscarPorTermo(String termo) {
        return tarefaRepository.buscarPorTermo(termo)
                .stream()
                .map(TarefaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TarefaResponseDTO> listarOrdenadas() {
        return tarefaRepository.findAllOrderByPrioridadeAndData()
                .stream()
                .map(TarefaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public TarefaResponseDTO criar(TarefaRequestDTO dto) {
        validarTarefa(dto);

        Tarefa tarefa = new Tarefa();
        tarefa.setTitulo(dto.getTitulo());
        tarefa.setDescricao(dto.getDescricao());
        tarefa.setStatus(dto.getStatus());
        tarefa.setPrioridade(dto.getPrioridade());

        Tarefa tarefaSalva = tarefaRepository.save(tarefa);
        return TarefaResponseDTO.fromEntity(tarefaSalva);
    }

    @Transactional
    public TarefaResponseDTO atualizar(Long id, TarefaRequestDTO dto) {
        validarTarefa(dto);

        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada com ID: " + id));

        if (tarefa.getStatus() == StatusTarefa.CONCLUIDA && dto.getStatus() != StatusTarefa.CONCLUIDA) {
            throw new BusinessException("Não é possível reabrir uma tarefa concluída");
        }

        tarefa.setTitulo(dto.getTitulo());
        tarefa.setDescricao(dto.getDescricao());
        tarefa.setStatus(dto.getStatus());
        tarefa.setPrioridade(dto.getPrioridade());

        Tarefa tarefaAtualizada = tarefaRepository.save(tarefa);
        return TarefaResponseDTO.fromEntity(tarefaAtualizada);
    }

    @Transactional
    public void deletar(Long id) {
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada com ID: " + id));

        tarefaRepository.delete(tarefa);
    }

    @Transactional
    public TarefaResponseDTO atualizarStatus(Long id, StatusTarefa novoStatus) {
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada com ID: " + id));

        if (tarefa.getStatus() == StatusTarefa.CONCLUIDA && novoStatus != StatusTarefa.CONCLUIDA) {
            throw new BusinessException("Não é possível reabrir uma tarefa concluída");
        }

        tarefa.setStatus(novoStatus);
        Tarefa tarefaAtualizada = tarefaRepository.save(tarefa);
        return TarefaResponseDTO.fromEntity(tarefaAtualizada);
    }

    private void validarTarefa(TarefaRequestDTO dto) {
        if (dto.getTitulo() == null || dto.getTitulo().trim().isEmpty()) {
            throw new BusinessException("O título da tarefa é obrigatório");
        }

        if (dto.getStatus() == null) {
            throw new BusinessException("O status da tarefa é obrigatório");
        }

        if (dto.getPrioridade() == null) {
            throw new BusinessException("A prioridade da tarefa é obrigatória");
        }
    }
}
