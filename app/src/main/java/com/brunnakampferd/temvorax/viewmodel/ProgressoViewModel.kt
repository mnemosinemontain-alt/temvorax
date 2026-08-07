package com.brunnakampferd.temvorax.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brunnakampferd.temvorax.data.repository.ProgressoRepository
import com.brunnakampferd.temvorax.domain.ResultadoStreak
import com.brunnakampferd.temvorax.domain.calcularStreak
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * Expõe o streak (atual/melhor) já calculado e pronto pra UI — a Home só
 * observa [streak], sem saber nada de como o cálculo é feito.
 */
class ProgressoViewModel(repository: ProgressoRepository) : ViewModel() {

    val streak: StateFlow<ResultadoStreak> = repository.observarDiasAtivos()
        .map { calcularStreak(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ResultadoStreak(atual = 0, melhor = 0))
}
