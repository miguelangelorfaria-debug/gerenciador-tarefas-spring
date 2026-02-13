package com.estagio.tarefas.repository;

import com.estagio.tarefas.model.Prioridade;
import com.estagio.tarefas.model.StatusTarefa;
import com.estagio.tarefas.model.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Long> {

    List<Tarefa> findByStatus(StatusTarefa status);

    List<Tarefa> findByPrioridade(Prioridade prioridade);

    List<Tarefa> findByStatusAndPrioridade(StatusTarefa status, Prioridade prioridade);

    @Query("SELECT t FROM Tarefa t WHERE LOWER(t.titulo) LIKE LOWER(CONCAT('%', :termo, '%')) " +
           "OR LOWER(t.descricao) LIKE LOWER(CONCAT('%', :termo, '%'))")
    List<Tarefa> buscarPorTermo(String termo);

    @Query("SELECT t FROM Tarefa t ORDER BY t.prioridade DESC, t.dataCriacao ASC")
    List<Tarefa> findAllOrderByPrioridadeAndData();
}
