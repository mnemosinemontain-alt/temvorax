package com.brunnakampferd.temvorax.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import com.brunnakampferd.temvorax.ui.theme.TealPrimario
import kotlinx.coroutines.delay

/**
 * Checkbox de "marcar como concluído" com um reforço visual leve — um
 * pequeno pulso — no instante em que vira concluído. É só isso: sem confete,
 * sem som, sem pontuação aparecendo na tela. A ideia é dar um feedback de
 * "isso contou" sem transformar marcar uma tarefa em um evento chamativo,
 * pro público do Temvorax (já sobrecarregado) não sentir que o app está
 * disputando atenção — ver análise de gamificação anti-culpa no plano.
 *
 * Reaproveitado nos 4 pontos de conclusão do app: Home ([CartaoTarefa]),
 * Tarefas diárias, Trabalhos acadêmicos e Atividades profissionais.
 */
@Composable
fun CheckboxComReforco(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var escalaAlvo by remember { mutableFloatStateOf(1f) }
    val escala by animateFloatAsState(
        targetValue = escalaAlvo,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "pulsoConclusao"
    )

    Checkbox(
        checked = checked,
        onCheckedChange = { novoValor ->
            if (novoValor) escalaAlvo = 1.35f
            onCheckedChange(novoValor)
        },
        colors = CheckboxDefaults.colors(checkedColor = TealPrimario),
        modifier = modifier.scale(escala)
    )

    LaunchedEffect(escalaAlvo) {
        if (escalaAlvo > 1f) {
            delay(120)
            escalaAlvo = 1f
        }
    }
}
