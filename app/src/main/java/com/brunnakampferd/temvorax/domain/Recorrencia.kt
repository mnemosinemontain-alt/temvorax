package com.brunnakampferd.temvorax.domain

import java.time.DayOfWeek
import java.time.LocalDate

/**
 * Um compromisso "ocorre" em [dataAlvo] se: for avulso ([diasSemana] vazio/null)
 * e a data bater exatamente, OU for recorrente ([diasSemana] preenchido) e
 * [dataAlvo] cair num dos dias marcados, dentro da janela de validade — da
 * data original (não conta recorrência ANTES de quando foi criado) até
 * [recorrenteAte], se houver.
 *
 * Compartilhado entre [calcularPlanejamento] (Planejamento Inteligente) e
 * [montarResultadoAgenda] (Agenda do Dia da Home) — mesma regra, dois lugares
 * diferentes que precisam saber "isso acontece hoje?".
 */
fun compromissoOcorreEm(
    dataOriginal: LocalDate,
    diasSemana: Set<DayOfWeek>?,
    recorrenteAte: LocalDate?,
    dataAlvo: LocalDate
): Boolean {
    if (diasSemana.isNullOrEmpty()) return dataOriginal == dataAlvo
    return dataAlvo.dayOfWeek in diasSemana &&
        !dataAlvo.isBefore(dataOriginal) &&
        (recorrenteAte == null || !dataAlvo.isAfter(recorrenteAte))
}
