package com.brunnakampferd.temvorax.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.brunnakampferd.temvorax.ui.theme.CremeCard
import com.brunnakampferd.temvorax.ui.theme.TextoPrincipal

/**
 * Balão de fala reutilizável do Koda.
 *
 * Eu criei esse componente separado porque o Koda vai falar em VÁRIAS telas do app
 * (Welcome, Home, conquistas, dicas...). Então em vez de duplicar esse bloco de UI
 * toda vez, eu só chamo <KodaSpeechBubble texto="..."> onde precisar.
 *
 * Tem uma animação sutil de "flutuação" (bobbing) pra dar vida ao balão, sem exagerar.
 */
@Composable
fun KodaSpeechBubble(
    texto: String,
    modifier: Modifier = Modifier
) {
    // Animação infinita e suave de sobe-desce, só pra dar aquele "respiro" de vida.
    val infiniteTransition = rememberInfiniteTransition(label = "koda_bubble_float")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offsetY"
    )

    Box(
        modifier = modifier
            .offset(y = offsetY.dp)
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(20.dp), clip = false)
            .background(color = CremeCard, shape = RoundedCornerShape(20.dp))
            .padding(horizontal = 18.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            color = TextoPrincipal,
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}