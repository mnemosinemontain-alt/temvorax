package com.brunnakampferd.temvorax.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brunnakampferd.temvorax.R
import com.brunnakampferd.temvorax.ui.components.BotaoPrimario
import com.brunnakampferd.temvorax.ui.components.BotaoSecundario
import com.brunnakampferd.temvorax.ui.components.LinkRodape
import com.brunnakampferd.temvorax.ui.theme.RoxoEscuro

/**
 * WelcomeScreen do Temvorax.
 *
 * MUDANÇA IMPORTANTE em relação à primeira versão: a arte "welcome_temvorax_clean"
 * já vem PRONTA com logo, balão do Koda, mascote e decorações — do topo até a faixa
 * de grama lá embaixo. Ou seja, essa imagem sozinha JÁ É o fundo inteiro da tela.
 *
 * Por isso, aqui eu NÃO desenho mais logo, balão ou Koda separados (isso já existe
 * dentro da imagem). Eu só desenho os elementos que precisam ser CLICÁVEIS de verdade:
 * os botões e os links. Eles ficam posicionados na área vazia (creme) que sobra no
 * meio/final da arte, alinhados na parte de baixo da tela.
 *
 * Se no seu aparelho os botões ficarem muito colados na faixa de grama do rodapé,
 * é só eu aumentar o "bottom" do padding do Column mais abaixo.
 *
 * @param onEntrarComGoogleClick callback disparado ao clicar em "Entrar com Google"
 * @param onCriarContaClick callback disparado ao clicar em "Criar conta"
 * @param onJaTenhoContaClick callback disparado ao clicar em "Já tenho conta"
 * @param onTermosClick callback disparado ao clicar em "Termos de Uso"
 * @param onPoliticaClick callback disparado ao clicar em "Política de Privacidade"
 */
@Composable
fun WelcomeScreen(
    onEntrarComGoogleClick: () -> Unit = {},
    onCriarContaClick: () -> Unit = {},
    onJaTenhoContaClick: () -> Unit = {},
    onTermosClick: () -> Unit = {},
    onPoliticaClick: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize()) {

        // ---------- CAMADA 1: a arte completa, cobrindo a tela inteira ----------
        // Alignment.TopCenter garante que o topo (logo + Koda + balão) NUNCA seja
        // cortado, mesmo em telas com proporção diferente da imagem original.
        // Se sobrar/faltar espaço, quem "sofre" o corte é a faixa de grama debaixo,
        // que é só decorativa (tudo bem perder um pedacinho dela).
        Image(
            painter = painterResource(id = R.drawable.welcome_temvorax_clean),
            contentDescription = "Koda te dando as boas-vindas ao Temvorax",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alignment = Alignment.TopCenter
        )

        // ---------- CAMADA 2: botões reais, sobrepostos na área vazia da arte ----------
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp)
                .padding(bottom = 56.dp), // distância do rodapé, pra não colar na faixa de grama
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BotaoPrimario(
                texto = "Entrar com Google",
                onClick = onEntrarComGoogleClick
            )

            Spacer(modifier = Modifier.height(12.dp))

            BotaoSecundario(
                texto = "Criar conta",
                onClick = onCriarContaClick
            )

            Spacer(modifier = Modifier.height(8.dp))

            // "Já tenho conta" fica mais discreto — ação secundária pra quem já é usuário.
            LinkRodape(
                texto = "Já tenho conta",
                onClick = onJaTenhoContaClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ---- Rodapé: Termos de Uso e Política de Privacidade ----
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LinkRodape(texto = "Termos de Uso", onClick = onTermosClick)
                Text(text = "•", color = RoxoEscuro, fontSize = 12.sp)
                LinkRodape(texto = "Política de Privacidade", onClick = onPoliticaClick)
            }
        }
    }
}

/**
 * Preview pra eu ver a tela direto no Android Studio sem precisar rodar no emulador.
 */
@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
fun WelcomeScreenPreview() {
    WelcomeScreen()
}