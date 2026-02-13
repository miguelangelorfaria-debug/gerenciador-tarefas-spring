package com.estagio.tarefas.service;

import com.estagio.tarefas.dto.TarefaRequestDTO;
import com.estagio.tarefas.dto.TarefaResponseDTO;
import com.estagio.tarefas.exception.BusinessException;
import com.estagio.tarefas.exception.ResourceNotFoundException;
import com.estagio.tarefas.model.Prioridade;
import com.estagio.tarefas.model.StatusTarefa;
import com.estagio.tarefas.model.Tarefa;
import com.estagio.tarefas.repository.TarefaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TarefaServiceTest {

    @Mock
    private TarefaRepository tarefaRepository;

    @InjectMocks
    private TarefaService tarefaService;

    private Tarefa tarefa;
    private TarefaRequestDTO tarefaRequestDTO;

    @BeforeEach
    void setUp() {
        tarefa = new Tarefa();
        tarefa.setId(1L);
        tarefa.setTitulo("Tarefa Teste");
        tarefa.setDescricao("Descrição da tarefa");
        tarefa.setStatus(StatusTarefa.PENDENTE);
        tarefa.setPrioridade(Prioridade.MEDIA);
        tarefa.setDataCriacao(LocalDateTime.now());
        tarefa.setDataAtualizacao(LocalDateTime.now());

        tarefaRequestDTO = new TarefaRequestDTO();
        tarefaRequestDTO.setTitulo("Tarefa Teste");
        tarefaRequestDTO.setDescricao("Descrição da tarefa");
        tarefaRequestDTO.setStatus(StatusTarefa.PENDENTE);
        tarefaRequestDTO.setPrioridade(Prioridade.MEDIA);
    }

    @Test
    @DisplayName("Deve listar todas as tarefas")
    void deveListarTodasAsTarefas() {
        Tarefa tarefa2 = new Tarefa();
        tarefa2.setId(2L);
        tarefa2.setTitulo("Tarefa 2");
        tarefa2.setStatus(StatusTarefa.EM_ANDAMENTO);
        tarefa2.setPrioridade(Prioridade.ALTA);

        when(tarefaRepository.findAll()).thenReturn(Arrays.asList(tarefa, tarefa2));

        List<TarefaResponseDTO> resultado = tarefaService.listarTodas();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(tarefaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar tarefa por ID com sucesso")
    void deveBuscarTarefaPorId() {
        when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));

        TarefaResponseDTO resultado = tarefaService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals("Tarefa Teste", resultado.getTitulo());
        assertEquals(StatusTarefa.PENDENTE, resultado.getStatus());
        verify(tarefaRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar tarefa inexistente")
    void deveLancarExcecaoAoBuscarTarefaInexistente() {
        when(tarefaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            tarefaService.buscarPorId(99L);
        });

        verify(tarefaRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Deve criar nova tarefa com sucesso")
    void deveCriarNovaTarefa() {
        when(tarefaRepository.save(any(Tarefa.class))).thenReturn(tarefa);

        TarefaResponseDTO resultado = tarefaService.criar(tarefaRequestDTO);

        assertNotNull(resultado);
        assertEquals("Tarefa Teste", resultado.getTitulo());
        assertEquals(StatusTarefa.PENDENTE, resultado.getStatus());
        verify(tarefaRepository, times(1)).save(any(Tarefa.class));
    }

    @Test
    @DisplayName("Deve atualizar tarefa com sucesso")
    void deveAtualizarTarefa() {
        when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));
        when(tarefaRepository.save(any(Tarefa.class))).thenReturn(tarefa);

        TarefaRequestDTO updateDTO = new TarefaRequestDTO();
        updateDTO.setTitulo("Tarefa Atualizada");
        updateDTO.setDescricao("Nova descrição");
        updateDTO.setStatus(StatusTarefa.EM_ANDAMENTO);
        updateDTO.setPrioridade(Prioridade.ALTA);

        TarefaResponseDTO resultado = tarefaService.atualizar(1L, updateDTO);

        assertNotNull(resultado);
        verify(tarefaRepository, times(1)).findById(1L);
        verify(tarefaRepository, times(1)).save(any(Tarefa.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao reabrir tarefa concluída")
    void deveLancarExcecaoAoReabrirTarefaConcluida() {
        tarefa.setStatus(StatusTarefa.CONCLUIDA);
        tarefa.setDataConclusao(LocalDateTime.now());

        when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));

        TarefaRequestDTO updateDTO = new TarefaRequestDTO();
        updateDTO.setTitulo("Tarefa Atualizada");
        updateDTO.setDescricao("Nova descrição");
        updateDTO.setStatus(StatusTarefa.PENDENTE);
        updateDTO.setPrioridade(Prioridade.ALTA);

        assertThrows(BusinessException.class, () -> {
            tarefaService.atualizar(1L, updateDTO);
        });

        verify(tarefaRepository, times(1)).findById(1L);
        verify(tarefaRepository, never()).save(any(Tarefa.class));
    }

    @Test
    @DisplayName("Deve deletar tarefa com sucesso")
    void deveDeletarTarefa() {
        when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));
        doNothing().when(tarefaRepository).delete(tarefa);

        tarefaService.deletar(1L);

        verify(tarefaRepository, times(1)).findById(1L);
        verify(tarefaRepository, times(1)).delete(tarefa);
    }

    @Test
    @DisplayName("Deve buscar tarefas por status")
    void deveBuscarTarefasPorStatus() {
        when(tarefaRepository.findByStatus(StatusTarefa.PENDENTE)).thenReturn(Arrays.asList(tarefa));

        List<TarefaResponseDTO> resultado = tarefaService.buscarPorStatus(StatusTarefa.PENDENTE);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(StatusTarefa.PENDENTE, resultado.get(0).getStatus());
        verify(tarefaRepository, times(1)).findByStatus(StatusTarefa.PENDENTE);
    }

    @Test
    @DisplayName("Deve buscar tarefas por prioridade")
    void deveBuscarTarefasPorPrioridade() {
        when(tarefaRepository.findByPrioridade(Prioridade.MEDIA)).thenReturn(Arrays.asList(tarefa));

        List<TarefaResponseDTO> resultado = tarefaService.buscarPorPrioridade(Prioridade.MEDIA);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(Prioridade.MEDIA, resultado.get(0).getPrioridade());
        verify(tarefaRepository, times(1)).findByPrioridade(Prioridade.MEDIA);
    }

    @Test
    @DisplayName("Deve atualizar status da tarefa")
    void deveAtualizarStatus() {
        when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));
        when(tarefaRepository.save(any(Tarefa.class))).thenReturn(tarefa);

        TarefaResponseDTO resultado = tarefaService.atualizarStatus(1L, StatusTarefa.EM_ANDAMENTO);

        assertNotNull(resultado);
        verify(tarefaRepository, times(1)).findById(1L);
        verify(tarefaRepository, times(1)).save(any(Tarefa.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar tarefa sem título")
    void deveLancarExcecaoAoCriarTarefaSemTitulo() {
        TarefaRequestDTO dtoInvalido = new TarefaRequestDTO();
        dtoInvalido.setTitulo("");
        dtoInvalido.setStatus(StatusTarefa.PENDENTE);
        dtoInvalido.setPrioridade(Prioridade.MEDIA);

        assertThrows(BusinessException.class, () -> {
            tarefaService.criar(dtoInvalido);
        });

        verify(tarefaRepository, never()).save(any(Tarefa.class));
    }
}
