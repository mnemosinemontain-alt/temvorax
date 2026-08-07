package com.brunnakampferd.temvorax.domain

import com.brunnakampferd.temvorax.data.model.AreaTarefa
import com.brunnakampferd.temvorax.data.model.AtividadeProfissional
import com.brunnakampferd.temvorax.data.model.CompromissoPessoal
import com.brunnakampferd.temvorax.data.model.CompromissoProfissional
import com.brunnakampferd.temvorax.data.model.Disciplina
import com.brunnakampferd.temvorax.data.model.Expediente
import com.brunnakampferd.temvorax.data.model.Prova
import com.brunnakampferd.temvorax.data.model.Semestre
import com.brunnakampferd.temvorax.data.model.TarefaDia
import com.brunnakampferd.temvorax.data.model.TarefaDiaria
import com.brunnakampferd.temvorax.data.model.TrabalhoAcademico
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

/** De qual tela/entidade veio um item da agenda — decide como exibir e (quando der) como concluir. */
enum class TipoItemAgenda {
    TAREFA_RAPIDA, TAREFA_DIARIA, AULA, PROVA, TRABALHO,
    EXPEDIENTE, COMPROMISSO_PROFISSIONAL, COMPROMISSO_PESSOAL, ATIVIDADE_PROFISSIONAL
}

/**
 * Um item pronto pra exibir na agenda unificada da Home. [concluida] é `null`
 * pra tipos que não têm esse conceito (Aula, Prova, Expediente, Compromissos —
 * ver decisão de produto: eles aparecem só como informação, sem toque fazendo
 * nada). [origem] guarda a entidade original — a Home usa [tipo] pra saber
 * pra qual ViewModel/entidade fazer o cast, na hora de marcar como concluído.
 */
data class ItemAgenda(
    val titulo: String,
    val area: AreaTarefa,
    val horaInicio: LocalTime?,
    val horaFim: LocalTime?,
    val tipo: TipoItemAgenda,
    val concluida: Boolean?,
    val origem: Any
)

/** Agenda já separada em duas seções, prontas pra exibir: com horário definido (ordenados) e sem. */
data class ResultadoAgenda(
    val comHorario: List<ItemAgenda>,
    val semHorario: List<ItemAgenda>
)

/** Junta itens de todas as áreas e separa em com/sem horário, ordenando os com horário. */
fun montarResultadoAgenda(itens: List<ItemAgenda>): ResultadoAgenda {
    val (comHorario, semHorario) = itens.partition { it.horaInicio != null }
    return ResultadoAgenda(
        comHorario = comHorario.sortedBy { it.horaInicio },
        semHorario = semHorario
    )
}

fun itensTarefaDia(hoje: LocalDate, tarefas: List<TarefaDia>): List<ItemAgenda> =
    tarefas.filter { it.data == hoje }.map {
        ItemAgenda(it.titulo, it.area, null, null, TipoItemAgenda.TAREFA_RAPIDA, it.concluida, it)
    }

/**
 * Aulas, provas e trabalhos só contam se pertencerem a um semestre com
 * `ativo = true` — mesma regra que o Planejamento Inteligente já usa (ver
 * [calcularPlanejamento]) — e Disciplina respeita o período próprio dela
 * dentro do semestre ([Disciplina.dataInicioMateria]/[Disciplina.dataFimMateria],
 * null = vale o semestre inteiro).
 */
fun itensAcademicosDoDia(
    hoje: LocalDate,
    semestres: List<Semestre>,
    disciplinas: List<Disciplina>,
    provas: List<Prova>,
    trabalhos: List<TrabalhoAcademico>
): List<ItemAgenda> {
    val idsAtivos = semestres.filter { it.ativo }.map { it.id }.toSet()

    val aulas = disciplinas.filter {
        it.semestreId in idsAtivos &&
            hoje.dayOfWeek in it.diasSemana &&
            (it.dataInicioMateria == null || !hoje.isBefore(it.dataInicioMateria)) &&
            (it.dataFimMateria == null || !hoje.isAfter(it.dataFimMateria))
    }.map { ItemAgenda(it.nome, AreaTarefa.ACADEMICA, it.horaInicio, it.horaFim, TipoItemAgenda.AULA, null, it) }

    val provasDoDia = provas.filter { it.semestreId in idsAtivos && it.data == hoje }
        .map { ItemAgenda(it.titulo, AreaTarefa.ACADEMICA, null, null, TipoItemAgenda.PROVA, null, it) }

    val trabalhosDoDia = trabalhos.filter { it.semestreId in idsAtivos && it.dataEntrega == hoje }
        .map { ItemAgenda(it.titulo, AreaTarefa.ACADEMICA, null, null, TipoItemAgenda.TRABALHO, it.concluido, it) }

    return aulas + provasDoDia + trabalhosDoDia
}

fun itensProfissionaisDoDia(
    hoje: LocalDate,
    expedientes: List<Expediente>,
    compromissos: List<CompromissoProfissional>,
    atividades: List<AtividadeProfissional>
): List<ItemAgenda> {
    val expedienteDoDia = expedientes.filter { hoje.dayOfWeek in it.diasSemana }
        .map { ItemAgenda("Expediente", AreaTarefa.PROFISSIONAL, it.horaInicio, it.horaFim, TipoItemAgenda.EXPEDIENTE, null, it) }

    val compromissosDoDia = compromissos.filter { compromissoOcorreEm(it.data, it.diasSemana, it.recorrenteAte, hoje) }
        .map { ItemAgenda(it.titulo, AreaTarefa.PROFISSIONAL, it.horaInicio, it.horaFim, TipoItemAgenda.COMPROMISSO_PROFISSIONAL, null, it) }

    val atividadesDoDia = atividades.filter { it.data == hoje }
        .map { ItemAgenda(it.titulo, AreaTarefa.PROFISSIONAL, null, null, TipoItemAgenda.ATIVIDADE_PROFISSIONAL, it.concluida, it) }

    return expedienteDoDia + compromissosDoDia + atividadesDoDia
}

fun itensPessoaisDoDia(
    hoje: LocalDate,
    tarefasDiarias: List<TarefaDiaria>,
    compromissos: List<CompromissoPessoal>
): List<ItemAgenda> {
    val tarefasDoDia = tarefasDiarias.filter { it.data == hoje }
        .map { ItemAgenda(it.titulo, AreaTarefa.PESSOAL, null, null, TipoItemAgenda.TAREFA_DIARIA, it.concluida, it) }

    val compromissosDoDia = compromissos.filter { compromissoOcorreEm(it.data, it.diasSemana, it.recorrenteAte, hoje) }
        .map { ItemAgenda(it.titulo, AreaTarefa.PESSOAL, it.horaInicio, it.horaFim, TipoItemAgenda.COMPROMISSO_PESSOAL, null, it) }

    return tarefasDoDia + compromissosDoDia
}

/**
 * Um bloco de rotina de uma área, pra tela "Hoje": uma linha de contexto fixo
 * (ex.: nome do semestre ativo, ou o horário do expediente) + o próximo item
 * com horário daquela área a partir de "agora" (ou já em andamento). Um bloco
 * só existe se tiver algo pra mostrar — sem contexto e sem próximo item, a
 * área nem aparece na tela, pra não virar um card vazio.
 */
data class BlocoRotina(
    val area: AreaTarefa,
    val titulo: String,
    val linhaContexto: String?,
    val proximoItem: ItemAgenda?
)

/**
 * O intervalo livre entre o fim de um item com horário e o início do próximo
 * — só a partir de "agora" em diante (uma janela que já passou inteira não
 * aparece; uma que já começou mas ainda não acabou aparece "encolhida",
 * começando em "agora" em vez de no horário original).
 */
data class JanelaLivre(
    val apos: ItemAgenda,
    val antes: ItemAgenda,
    val inicio: LocalTime,
    val duracaoMinutos: Long
)

/** Primeiro item da lista (com horário) que ainda não terminou — em andamento ou por vir. */
private fun proximoOuAtual(itens: List<ItemAgenda>, agora: LocalTime): ItemAgenda? =
    itens.filter { it.horaInicio != null && (it.horaFim == null || !it.horaFim.isBefore(agora)) }
        .minByOrNull { it.horaInicio!! }

/**
 * Monta os blocos "Rotina Universitária/Profissional/Pessoal" a partir dos
 * itens COM horário de hoje (já vêm de [montarResultadoAgenda]) + a lista de
 * semestres (pra saber o nome do(s) ativo(s)).
 *
 * Expediente vira a "linha de contexto" do bloco Profissional (é o horário
 * fixo do dia, não "o próximo compromisso") — por isso fica de fora de novo
 * quando calculamos o "próximo item" dessa área, pra não repetir a mesma
 * informação duas vezes no mesmo bloco.
 */
fun montarBlocosRotina(itensComHorario: List<ItemAgenda>, semestresAtivos: List<Semestre>, agora: LocalTime): List<BlocoRotina> {
    val blocos = mutableListOf<BlocoRotina>()

    val itensAcademicos = itensComHorario.filter { it.area == AreaTarefa.ACADEMICA }
    val contextoAcademico = semestresAtivos.joinToString(" • ") { it.nome }.ifBlank { null }
    val proximaAula = proximoOuAtual(itensAcademicos.filter { it.tipo == TipoItemAgenda.AULA }, agora)
    if (contextoAcademico != null || proximaAula != null) {
        blocos += BlocoRotina(AreaTarefa.ACADEMICA, "Rotina Universitária", contextoAcademico, proximaAula)
    }

    val itensProfissionais = itensComHorario.filter { it.area == AreaTarefa.PROFISSIONAL }
    val expedienteHoje = itensProfissionais.firstOrNull { it.tipo == TipoItemAgenda.EXPEDIENTE }
    val contextoProfissional = expedienteHoje?.let { "Expediente: ${it.horaInicio}–${it.horaFim}" }
    val proximoCompromissoProfissional =
        proximoOuAtual(itensProfissionais.filter { it.tipo == TipoItemAgenda.COMPROMISSO_PROFISSIONAL }, agora)
    if (contextoProfissional != null || proximoCompromissoProfissional != null) {
        blocos += BlocoRotina(AreaTarefa.PROFISSIONAL, "Rotina Profissional", contextoProfissional, proximoCompromissoProfissional)
    }

    val proximoCompromissoPessoal =
        proximoOuAtual(itensComHorario.filter { it.area == AreaTarefa.PESSOAL }, agora)
    if (proximoCompromissoPessoal != null) {
        blocos += BlocoRotina(AreaTarefa.PESSOAL, "Rotina Pessoal", null, proximoCompromissoPessoal)
    }

    return blocos
}

/**
 * Janelas livres entre itens consecutivos com horário, só a partir de
 * "agora" em diante (decisão de produto: janela que já passou não aparece).
 */
fun calcularJanelasLivres(itensComHorario: List<ItemAgenda>, agora: LocalTime): List<JanelaLivre> {
    val ordenados = itensComHorario.filter { it.horaInicio != null && it.horaFim != null }.sortedBy { it.horaInicio }
    val janelas = mutableListOf<JanelaLivre>()

    for (i in 0 until ordenados.size - 1) {
        val atual = ordenados[i]
        val proximo = ordenados[i + 1]
        val fimAtual = atual.horaFim!!
        val inicioProximo = proximo.horaInicio!!
        val inicioJanela = if (fimAtual.isAfter(agora)) fimAtual else agora
        if (inicioJanela.isBefore(inicioProximo)) {
            val duracao = Duration.between(inicioJanela, inicioProximo).toMinutes()
            if (duracao > 0) janelas += JanelaLivre(atual, proximo, inicioJanela, duracao)
        }
    }

    return janelas
}
