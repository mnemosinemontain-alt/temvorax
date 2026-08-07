package com.brunnakampferd.temvorax.ui.screens.legal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brunnakampferd.temvorax.ui.theme.CremeClaro
import com.brunnakampferd.temvorax.ui.theme.RoxoPrimario
import com.brunnakampferd.temvorax.ui.theme.TextoPrincipal

/**
 * Tela genérica pra exibir um texto jurídico (Termos de Uso ou Política de Privacidade).
 *
 * Eu fiz UMA tela só e reutilizável (em vez de TermosScreen.kt e PoliticaScreen.kt
 * separadas) porque as duas são estruturalmente idênticas: título + texto rolável +
 * botão de voltar. Só o conteúdo muda. Assim, se eu quiser ajustar o visual dessa
 * tela no futuro, mudo em um lugar só.
 *
 * @param titulo "Termos de Uso" ou "Política de Privacidade"
 * @param conteudo o texto completo (vem de LegalContent.kt)
 * @param onVoltarClick chamado ao clicar na seta de voltar
 */
@Composable
fun TextoLegalScreen(
    titulo: String,
    conteudo: String,
    onVoltarClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CremeClaro)
    ) {
        // ---- Cabeçalho com botão de voltar ----
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            IconButton(onClick = onVoltarClick) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = RoxoPrimario
                )
            }
            Text(
                text = titulo,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextoPrincipal
            )
        }

        // ---- Corpo do texto, rolável ----
        Text(
            text = conteudo,
            fontSize = 14.sp,
            color = TextoPrincipal,
            lineHeight = 22.sp,
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        )
    }
}