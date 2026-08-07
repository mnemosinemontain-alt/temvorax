package com.brunnakampferd.temvorax.ui.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val FORMATO_DATA_CURTA = DateTimeFormatter.ofPattern("dd/MM/yyyy")
private val FORMATO_HORA_CURTA = DateTimeFormatter.ofPattern("HH:mm")

fun LocalDate.formatarCurta(): String = format(FORMATO_DATA_CURTA)
fun LocalTime.formatarCurta(): String = format(FORMATO_HORA_CURTA)

fun nomeDiaCurto(dia: DayOfWeek): String = when (dia) {
    DayOfWeek.MONDAY -> "Seg"
    DayOfWeek.TUESDAY -> "Ter"
    DayOfWeek.WEDNESDAY -> "Qua"
    DayOfWeek.THURSDAY -> "Qui"
    DayOfWeek.FRIDAY -> "Sex"
    DayOfWeek.SATURDAY -> "Sáb"
    DayOfWeek.SUNDAY -> "Dom"
}

/** Ex.: um conjunto {QUARTA, SEGUNDA} vira "Seg, Qua" — sempre na ordem natural da semana. */
fun formatarDiasSemana(dias: Set<DayOfWeek>): String =
    dias.sorted().joinToString(", ") { nomeDiaCurto(it) }

/**
 * Subtítulo padrão de um compromisso (Pessoal/Profissional): "Toda Ter • 15:00"
 * se for recorrente, ou a data exata se for avulso — mesma lógica nos dois lugares.
 */
fun subtituloCompromisso(diasSemana: Set<DayOfWeek>?, data: LocalDate, horaInicio: LocalTime?): String {
    val horario = horaInicio?.let { " • $it" } ?: ""
    return if (diasSemana.isNullOrEmpty()) data.formatarCurta() + horario else "Toda ${formatarDiasSemana(diasSemana)}$horario"
}

fun nomeDiaCompleto(dia: DayOfWeek): String = when (dia) {
    DayOfWeek.MONDAY -> "Segunda-feira"
    DayOfWeek.TUESDAY -> "Terça-feira"
    DayOfWeek.WEDNESDAY -> "Quarta-feira"
    DayOfWeek.THURSDAY -> "Quinta-feira"
    DayOfWeek.FRIDAY -> "Sexta-feira"
    DayOfWeek.SATURDAY -> "Sábado"
    DayOfWeek.SUNDAY -> "Domingo"
}
