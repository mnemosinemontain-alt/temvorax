package com.brunnakampferd.temvorax.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brunnakampferd.temvorax.data.model.AreaTarefa
import com.brunnakampferd.temvorax.data.model.CompromissoPessoal
import com.brunnakampferd.temvorax.data.model.TarefaDiaria
import com.brunnakampferd.temvorax.data.model.TipoConclusao
import com.brunnakampferd.temvorax.data.repository.PessoalRepository
import com.brunnakampferd.temvorax.data.repository.ProgressoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PessoalViewModel(
    private val repository: PessoalRepository,
    private val progressoRepository: ProgressoRepository
) : ViewModel() {

    val tarefasDiarias: StateFlow<List<TarefaDiaria>> = repository.observarTarefasDiarias()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val compromissos: StateFlow<List<CompromissoPessoal>> = repository.observarCompromissos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun salvarTarefaDiaria(item: TarefaDiaria) = viewModelScope.launch { repository.salvarTarefaDiaria(item) }
    fun alternarTarefaDiaria(item: TarefaDiaria) = viewModelScope.launch {
        val concluida = !item.concluida
        repository.salvarTarefaDiaria(item.copy(concluida = concluida))
        // TarefaDiaria não tem campo de área próprio — é sempre Pessoal, pelo hub onde vive.
        if (concluida) {
            progressoRepository.registrarConclusao(TipoConclusao.TAREFA_DIARIA, item.id, AreaTarefa.PESSOAL)
        } else {
            progressoRepository.desfazerConclusao(TipoConclusao.TAREFA_DIARIA, item.id)
        }
    }
    fun removerTarefaDiaria(item: TarefaDiaria) = viewModelScope.launch { repository.removerTarefaDiaria(item) }

    fun salvarCompromisso(item: CompromissoPessoal) = viewModelScope.launch { repository.salvarCompromisso(item) }
    fun removerCompromisso(item: CompromissoPessoal) = viewModelScope.launch { repository.removerCompromisso(item) }
}
