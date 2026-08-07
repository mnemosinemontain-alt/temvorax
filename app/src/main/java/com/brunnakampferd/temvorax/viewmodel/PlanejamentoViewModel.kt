package com.brunnakampferd.temvorax.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brunnakampferd.temvorax.data.repository.AcademicoRepository
import com.brunnakampferd.temvorax.data.repository.PessoalRepository
import com.brunnakampferd.temvorax.data.repository.ProfissionalRepository
import com.brunnakampferd.temvorax.domain.PlanejamentoResultado
import com.brunnakampferd.temvorax.domain.calcularPlanejamento
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * ViewModel do Planejamento Inteligente. Diferente dos outros, não fica
 * observando os dados ao vivo — calcula sob demanda (chamada explícita de
 * `carregar()`, feita quando a tela abre) porque o cálculo cruza várias
 * fontes (disciplinas de todos os semestres ativos + expediente +
 * compromissos + provas/trabalhos) e refazer isso a cada mudancinha em
 * qualquer uma delas seria caro e desnecessário pra um painel que o usuário
 * só olha de vez em quando.
 */
class PlanejamentoViewModel(
    private val academico: AcademicoRepository,
    private val profissional: ProfissionalRepository,
    private val pessoal: PessoalRepository
) : ViewModel() {

    private val _resultado = MutableStateFlow<PlanejamentoResultado?>(null)
    val resultado: StateFlow<PlanejamentoResultado?> = _resultado.asStateFlow()

    private val _carregando = MutableStateFlow(false)
    val carregando: StateFlow<Boolean> = _carregando.asStateFlow()

    fun carregar() {
        viewModelScope.launch {
            _carregando.value = true

            val semestresAtivos = academico.observarSemestres().first().filter { it.ativo }
            val disciplinas = semestresAtivos.flatMap { academico.observarDisciplinas(it.id).first() }
            val provas = semestresAtivos.flatMap { academico.observarProvas(it.id).first() }
            val trabalhos = semestresAtivos.flatMap { academico.observarTrabalhos(it.id).first() }
            val expedientes = profissional.observarExpedientes().first()
            val compromissosProfissionais = profissional.observarCompromissos().first()
            val compromissosPessoais = pessoal.observarCompromissos().first()

            _resultado.value = calcularPlanejamento(
                hoje = LocalDate.now(),
                disciplinas = disciplinas,
                expedientes = expedientes,
                compromissosProfissionais = compromissosProfissionais,
                compromissosPessoais = compromissosPessoais,
                provas = provas,
                trabalhos = trabalhos
            )
            _carregando.value = false
        }
    }
}
