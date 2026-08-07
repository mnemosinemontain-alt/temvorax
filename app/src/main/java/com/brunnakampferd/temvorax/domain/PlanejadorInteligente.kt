package com.brunnakampferd.temvorax.domain

import com.brunnakampferd.temvorax.data.model.CompromissoPessoal
import com.brunnakampferd.temvorax.data.model.CompromissoProfissional
import com.brunnakampferd.temvorax.data.model.Disciplina
import com.brunnakampferd.temvorax.data.model.Expediente
import com.brunnakampferd.temvorax.data.model.Prova
import com.brunnakampferd.temvorax.data.model.TrabalhoAcademico
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters

data class AlertaPlanejamento(val titulo: String, val descricao: String)

data class PlanejamentoResultado(
    val inicioSemana: LocalDate,
    val fimSemana: LocalDate,
    val horasOcupadasSemana: Double,
    val horasLivresSemana: Double,
    val ocupacaoPorDia: Map<DayOfWeek, Double>,
    val alertas: List<AlertaPlanejamento>
)

private const val HORAS_ACORDADO_POR_DIA = 16.0
private const val HORAS_ACORDADO_SEMANA = HORAS_ACORDADO_POR_DIA * 7
private const val LIMIAR_POUCAS_HORAS_LIVRES = 10.0
private const val LIMIAR_AGENDA_CHEIA_PRAZO = 15.0
private const val DIAS_ANTECEDENCIA_PRAZO = 3L
private const val MULTIPLICADOR_SEMANA_SOBRECARREGADA = 1.3

private val DIAS_DA_SEMANA = DayOfWeek.values().toList()

/**
 * Calcula, a partir do que já foi cadastrado, quantas horas da semana atual
 * estão ocupadas e quantas sobram livres, e gera alertas simples.
 *
 * SIMPLIFICAÇÃO ASSUMIDA (v1): como o app ainda não guarda histórico de
 * semanas passadas, o alerta "semana bem mais cheia que o normal" compara a
 * semana atual (que inclui os compromissos avulsos) contra a carga FIXA/
 * recorrente (disciplinas + expediente) como linha de base — não contra uma
 * média histórica de verdade. Isso melhora quando o app acumular semanas.
 *
 * A "janela acordada" usada nas contas é de 16h por dia (112h/semana) — um
 * valor fixo por enquanto, sem personalização por usuário.
 */
fun calcularPlanejamento(
    hoje: LocalDate,
    disciplinas: List<Disciplina>,
    expedientes: List<Expediente>,
    compromissosProfissionais: List<CompromissoProfissional>,
    compromissosPessoais: List<CompromissoPessoal>,
    provas: List<Prova>,
    trabalhos: List<TrabalhoAcademico>
): PlanejamentoResultado {
    val inicioSemana = hoje.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val fimSemana = inicioSemana.plusDays(6)

    val ocupacaoRecorrentePorDia = DIAS_DA_SEMANA.associateWith { dia ->
        val data = inicioSemana.plusDays((dia.value - 1).toLong())
        horasDisciplinasDoDia(disciplinas, dia, data) + horasExpedienteDoDia(expedientes, dia)
    }

    val ocupacaoPorDia = DIAS_DA_SEMANA.associateWith { dia ->
        val data = inicioSemana.plusDays((dia.value - 1).toLong())
        (ocupacaoRecorrentePorDia[dia] ?: 0.0) +
            horasCompromissosProfissionais(compromissosProfissionais, data) +
            horasCompromissosPessoais(compromissosPessoais, data)
    }

    val horasOcupadasSemana = ocupacaoPorDia.values.sum()
    val horasLivresSemana = (HORAS_ACORDADO_SEMANA - horasOcupadasSemana).coerceAtLeast(0.0)
    val baselineRecorrente = ocupacaoRecorrentePorDia.values.sum()

    val alertas = mutableListOf<AlertaPlanejamento>()

    if (horasLivresSemana < LIMIAR_POUCAS_HORAS_LIVRES) {
        alertas += AlertaPlanejamento(
            titulo = "Poucas horas livres essa semana",
            descricao = "Só sobram ${formatarHoras(horasLivresSemana)} livres essa semana."
        )
    }

    if (baselineRecorrente > 0 && horasOcupadasSemana > baselineRecorrente * MULTIPLICADOR_SEMANA_SOBRECARREGADA) {
        alertas += AlertaPlanejamento(
            titulo = "Semana bem mais cheia que o normal",
            descricao = "Sua carga fixa de toda semana é ${formatarHoras(baselineRecorrente)}, mas essa semana está em ${formatarHoras(horasOcupadasSemana)} — os compromissos avulsos pesaram bastante."
        )
    }

    DIAS_DA_SEMANA.forEach { dia ->
        val ocupado = ocupacaoPorDia[dia] ?: 0.0
        if (ocupado >= HORAS_ACORDADO_POR_DIA) {
            alertas += AlertaPlanejamento(
                titulo = "${nomeDia(dia)} sem nenhum horário livre",
                descricao = "Esse dia já soma ${formatarHoras(ocupado)} entre aulas, expediente e compromissos."
            )
        }
    }

    if (horasLivresSemana < LIMIAR_AGENDA_CHEIA_PRAZO) {
        provas.forEach { prova ->
            val dias = ChronoUnit.DAYS.between(hoje, prova.data)
            if (dias in 0L..DIAS_ANTECEDENCIA_PRAZO) {
                alertas += AlertaPlanejamento(
                    titulo = "Prova perto do prazo, agenda cheia",
                    descricao = "\"${prova.titulo}\" é em $dias dia(s) e sua semana está com pouca folga."
                )
            }
        }
        trabalhos.filter { !it.concluido }.forEach { trabalho ->
            val dias = ChronoUnit.DAYS.between(hoje, trabalho.dataEntrega)
            if (dias in 0L..DIAS_ANTECEDENCIA_PRAZO) {
                alertas += AlertaPlanejamento(
                    titulo = "Trabalho perto do prazo, agenda cheia",
                    descricao = "\"${trabalho.titulo}\" vence em $dias dia(s) e sua semana está com pouca folga."
                )
            }
        }
    }

    return PlanejamentoResultado(inicioSemana, fimSemana, horasOcupadasSemana, horasLivresSemana, ocupacaoPorDia, alertas)
}

private fun horasDisciplinasDoDia(disciplinas: List<Disciplina>, dia: DayOfWeek, data: LocalDate): Double =
    disciplinas.filter {
        dia in it.diasSemana && it.horaInicio != null && it.horaFim != null &&
            (it.dataInicioMateria == null || !data.isBefore(it.dataInicioMateria)) &&
            (it.dataFimMateria == null || !data.isAfter(it.dataFimMateria))
    }.sumOf { horasBloco(it.horaInicio!!, it.horaFim!!) }

private fun horasExpedienteDoDia(expedientes: List<Expediente>, dia: DayOfWeek): Double =
    expedientes.filter { dia in it.diasSemana }
        .sumOf { horasBloco(it.horaInicio, it.horaFim) }

private fun horasCompromissosProfissionais(compromissos: List<CompromissoProfissional>, data: LocalDate): Double =
    compromissos.filter {
        compromissoOcorreEm(it.data, it.diasSemana, it.recorrenteAte, data) && it.horaInicio != null && it.horaFim != null
    }.sumOf { horasBloco(it.horaInicio!!, it.horaFim!!) }

private fun horasCompromissosPessoais(compromissos: List<CompromissoPessoal>, data: LocalDate): Double =
    compromissos.filter {
        compromissoOcorreEm(it.data, it.diasSemana, it.recorrenteAte, data) && it.horaInicio != null && it.horaFim != null
    }.sumOf { horasBloco(it.horaInicio!!, it.horaFim!!) }

private fun horasBloco(inicio: LocalTime, fim: LocalTime): Double {
    val minutos = Duration.between(inicio, fim).toMinutes()
    return if (minutos > 0) minutos / 60.0 else 0.0
}

fun formatarHoras(horas: Double): String {
    val h = horas.toInt()
    val min = ((horas - h) * 60).toInt()
    return if (min == 0) "${h}h" else "${h}h${min}min"
}

private fun nomeDia(dia: DayOfWeek): String = when (dia) {
    DayOfWeek.MONDAY -> "Segunda-feira"
    DayOfWeek.TUESDAY -> "Terça-feira"
    DayOfWeek.WEDNESDAY -> "Quarta-feira"
    DayOfWeek.THURSDAY -> "Quinta-feira"
    DayOfWeek.FRIDAY -> "Sexta-feira"
    DayOfWeek.SATURDAY -> "Sábado"
    DayOfWeek.SUNDAY -> "Domingo"
}
