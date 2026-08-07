package com.brunnakampferd.temvorax.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brunnakampferd.temvorax.data.repository.AcademicoRepository
import com.brunnakampferd.temvorax.data.repository.PessoalRepository
import com.brunnakampferd.temvorax.data.repository.ProfissionalRepository
import com.brunnakampferd.temvorax.data.repository.TarefaRepository
import com.brunnakampferd.temvorax.domain.BlocoRotina
import com.brunnakampferd.temvorax.domain.ItemAgenda
import com.brunnakampferd.temvorax.domain.JanelaLivre
import com.brunnakampferd.temvorax.domain.calcularJanelasLivres
import com.brunnakampferd.temvorax.domain.itensTarefaDia
import com.brunnakampferd.temvorax.domain.montarBlocosRotina
import com.brunnakampferd.temvorax.domain.montarResultadoAgenda
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.LocalTime

/**
 * Estado pronto pra tela "Hoje": os 3 blocos de rotina (Universitária/
 * Profissional/Pessoal), as janelas livres entre os compromissos com horário,
 * e as tarefas sem horário definido (tarefa rápida, diária, trabalho, atividade).
 */
data class EstadoAgendaHoje(
    val blocos: List<BlocoRotina>,
    val janelas: List<JanelaLivre>,
    val tarefasSemHorario: List<ItemAgenda>
)

/**
 * Junta tarefas rápidas + o que cada área já calcula como "itens de hoje" e
 * monta os blocos de rotina + janelas livres — é o que dá pra aba "Hoje" da
 * Home mostrar tudo agrupado por contexto em vez de uma lista solta.
 *
 * "Agora" é capturado uma vez só, na hora que a tela abre/volta a aparecer
 * (ver `agendaDoDiaViewModel()` em ViewModelProviders.kt) — não fica se
 * atualizando sozinho enquanto a tela está aberta (decisão de produto: mais
 * simples, recalcula de novo na próxima vez que a Home aparecer).
 *
 * Só observa/agrega (lado leitura) — concluir um item continua chamando o
 * ViewModel dono daquele tipo (TarefaViewModel, AcademicoViewModel etc.),
 * pra não duplicar a lógica de conclusão (incluindo o registro de streak)
 * que já existe em cada um deles.
 */
class AgendaDoDiaViewModel(
    tarefaRepository: TarefaRepository,
    academicoRepository: AcademicoRepository,
    profissionalRepository: ProfissionalRepository,
    pessoalRepository: PessoalRepository,
    hoje: LocalDate,
    agora: LocalTime
) : ViewModel() {

    val estado: StateFlow<EstadoAgendaHoje> = combine(
        tarefaRepository.observarTodas(),
        academicoRepository.observarItensAcademicosDoDia(hoje),
        profissionalRepository.observarItensProfissionaisDoDia(hoje),
        pessoalRepository.observarItensPessoaisDoDia(hoje),
        academicoRepository.observarSemestres()
    ) { tarefas, academicos, profissionais, pessoais, semestres ->
        val todosItens = itensTarefaDia(hoje, tarefas) + academicos + profissionais + pessoais
        val resultado = montarResultadoAgenda(todosItens)
        val semestresAtivos = semestres.filter { it.ativo }
        EstadoAgendaHoje(
            blocos = montarBlocosRotina(resultado.comHorario, semestresAtivos, agora),
            janelas = calcularJanelasLivres(resultado.comHorario, agora),
            tarefasSemHorario = resultado.semHorario
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        EstadoAgendaHoje(emptyList(), emptyList(), emptyList())
    )
}
