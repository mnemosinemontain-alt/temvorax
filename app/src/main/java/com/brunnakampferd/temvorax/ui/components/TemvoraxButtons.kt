package com.brunnakampferd.temvorax.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brunnakampferd.temvorax.ui.theme.*

/**
 * Botão primário do Temvorax (preenchido, roxo).
 *
 * Uso: ações principais, tipo "Entrar com Google" ou "Criar conta".
 * Eu botei uma sombra leve embaixo pra dar aquela sensação de "botão physical",
 * meio jogo indie, sem ficar pesado ou old-school demais.
 */
@Composable
fun BotaoPrimario(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icone: (@Composable () -> Unit)? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = RoxoPrimario,
            contentColor = TextoSobreRoxo
        )
    ) {
        if (icone != null) {
            icone()
            Spacer(modifier = Modifier.width(10.dp))
        }
        Text(text = texto, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
    }
}

/**
 * Botão secundário (contornado, transparente por dentro).
 *
 * Uso: ações alternativas, tipo "Já tenho conta". Ele não compete visualmente
 * com o botão primário, mas ainda assim parece clicável e "premium".
 */
@Composable
fun BotaoSecundario(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, RoxoPrimario),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = RoxoPrimario
        )
    ) {
        Text(text = texto, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
    }
}

/**
 * Link de texto simples (usado no rodapé: "Termos de Uso", "Política de Privacidade").
 *
 * É um TextButton bem discreto, sem fundo, só pra não poluir visualmente o rodapé.
 */
@Composable
fun LinkRodape(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(onClick = onClick, modifier = modifier) {
        Text(
            text = texto,
            color = TextoSecundario,
            fontSize = 12.sp,
            style = MaterialTheme.typography.labelSmall
        )
    }
}