package com.brunnakampferd.temvorax.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brunnakampferd.temvorax.data.model.AreaTarefa
import com.brunnakampferd.temvorax.data.model.TarefaDia
import com.brunnakampferd.temvorax.data.model.TipoConclusao
import com.brunnakampferd.temvorax.data.repository.ProgressoRepository
import com.brunnakampferd.temvorax.data.repository.TarefaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * ViewModel das tarefas rápidas de Hoje/Pendências/Concluído. As tarefas vêm
 * do Room (via TarefaRepository) — nada de dado fictício aqui, a lista
 * começa vazia até o usuário criar algo com o botão "+".
 */
class TarefaViewModel(
    private val repository: TarefaRepository,
    private val progressoRepository: ProgressoRepository
) : ViewModel() {

    val tarefas: StateFlow<List<TarefaDia>> = repository.observarTodas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun adicionar(titulo: String, area: AreaTarefa, data: LocalDate) {
        if (titulo.isBlank()) return
        viewModelScope.launch {
            repository.salvar(TarefaDia(titulo = titulo.trim(), area = area, data = data))
        }
    }

    fun alternarConcluida(tarefa: TarefaDia) {
        viewModelScope.launch {
            val concluida = !tarefa.concluida
            repository.salvar(tarefa.copy(concluida = concluida))
            if (concluida) {
                progressoRepository.registrarConclusao(TipoConclusao.TAREFA_DIA, tarefa.id, tarefa.area)
            } else {
                progressoRepository.desfazerConclusao(TipoConclusao.TAREFA_DIA, tarefa.id)
            }
        }
    }

    fun remover(tarefa: TarefaDia) {
        viewModelScope.launch { repository.remover(tarefa) }
    }
}
