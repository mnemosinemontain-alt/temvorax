package com.brunnakampferd.temvorax.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brunnakampferd.temvorax.data.model.AreaTarefa
import com.brunnakampferd.temvorax.data.model.AtividadeProfissional
import com.brunnakampferd.temvorax.data.model.CompromissoProfissional
import com.brunnakampferd.temvorax.data.model.Expediente
import com.brunnakampferd.temvorax.data.model.TipoConclusao
import com.brunnakampferd.temvorax.data.repository.ProfissionalRepository
import com.brunnakampferd.temvorax.data.repository.ProgressoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfissionalViewModel(
    private val repository: ProfissionalRepository,
    private val progressoRepository: ProgressoRepository
) : ViewModel() {

    val expedientes: StateFlow<List<Expediente>> = repository.observarExpedientes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val compromissos: StateFlow<List<CompromissoProfissional>> = repository.observarCompromissos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val atividades: StateFlow<List<AtividadeProfissional>> = repository.observarAtividades()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun salvarExpediente(item: Expediente) = viewModelScope.launch { repository.salvarExpediente(item) }
    fun removerExpediente(item: Expediente) = viewModelScope.launch { repository.removerExpediente(item) }

    fun salvarCompromisso(item: CompromissoProfissional) = viewModelScope.launch { repository.salvarCompromisso(item) }
    fun removerCompromisso(item: CompromissoProfissional) = viewModelScope.launch { repository.removerCompromisso(item) }

    fun salvarAtividade(item: AtividadeProfissional) = viewModelScope.launch { repository.salvarAtividade(item) }
    fun alternarAtividade(item: AtividadeProfissional) = viewModelScope.launch {
        val concluida = !item.concluida
        repository.salvarAtividade(item.copy(concluida = concluida))
        if (concluida) {
            progressoRepository.registrarConclusao(TipoConclusao.ATIVIDADE_PROFISSIONAL, item.id, AreaTarefa.PROFISSIONAL)
        } else {
            progressoRepository.desfazerConclusao(TipoConclusao.ATIVIDADE_PROFISSIONAL, item.id)
        }
    }
    fun removerAtividade(item: AtividadeProfissional) = viewModelScope.launch { repository.removerAtividade(item) }
}
