package com.estagio.tarefas.dto;

import com.estagio.tarefas.model.Prioridade;
import com.estagio.tarefas.model.StatusTarefa;
import com.estagio.tarefas.model.Tarefa;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TarefaResponseDTO {

    private Long id;
    private String titulo;
    private String descricao;
    private StatusTarefa status;
    private Prioridade prioridade;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private LocalDateTime dataConclusao;

    public static TarefaResponseDTO fromEntity(Tarefa tarefa) {
        return new TarefaResponseDTO(
            tarefa.getId(),
            tarefa.getTitulo(),
            tarefa.getDescricao(),
            tarefa.getStatus(),
            tarefa.getPrioridade(),
            tarefa.getDataCriacao(),
            tarefa.getDataAtualizacao(),
            tarefa.getDataConclusao()
        );
    }
}
