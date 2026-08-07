package com.brunnakampferd.temvorax.domain

import java.time.LocalDate

/**
 * Resultado do cálculo de streak: [atual] é a sequência de dias ativos
 * terminando hoje/ontem (0 se quebrou); [melhor] é a maior sequência já
 * alcançada, mesmo que o streak atual tenha zerado — serve de referência
 * neutra, nunca de cobrança (ver regra de exibição anti-culpa na Home).
 */
data class ResultadoStreak(val atual: Int, val melhor: Int)

/**
 * Calcula streak a partir dos dias distintos que tiveram pelo menos uma
 * conclusão registrada. [diasAtivos] não precisa vir ordenado/deduplicado —
 * a função cuida disso, então é seguro passar direto o que vier do Room.
 *
 * "Atual" só conta a partir de hoje ou ontem: se o dia mais recente ativo for
 * mais antigo que isso, a sequência foi quebrada e vira 0 — sem alarde na UI,
 * é só o reflexo de que não há sequência valendo agora (ver regra anti-culpa
 * em HomeScreen: o chip de streak simplesmente some quando atual == 0, em vez
 * de anunciar a quebra).
 */
fun calcularStreak(diasAtivos: List<LocalDate>, hoje: LocalDate = LocalDate.now()): ResultadoStreak {
    val dias = diasAtivos.distinct().sortedDescending()
    if (dias.isEmpty()) return ResultadoStreak(atual = 0, melhor = 0)

    // Maior sequência de dias consecutivos em toda a lista (o "recorde").
    var melhor = 1
    var sequenciaCorrente = 1
    for (i in 1 until dias.size) {
        sequenciaCorrente = if (dias[i - 1].minusDays(1) == dias[i]) sequenciaCorrente + 1 else 1
        if (sequenciaCorrente > melhor) melhor = sequenciaCorrente
    }

    // Sequência atual: só conta se o dia mais recente ativo for hoje ou ontem.
    val atual = if (dias.first().isBefore(hoje.minusDays(1))) {
        0
    } else {
        var contagem = 1
        for (i in 1 until dias.size) {
            if (dias[i - 1].minusDays(1) == dias[i]) contagem++ else break
        }
        contagem
    }

    return ResultadoStreak(atual = atual, melhor = maxOf(melhor, atual))
}
