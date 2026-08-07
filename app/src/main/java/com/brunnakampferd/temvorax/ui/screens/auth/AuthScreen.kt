package com.brunnakampferd.temvorax.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.brunnakampferd.temvorax.ui.components.BotaoSecundario
import com.brunnakampferd.temvorax.ui.components.LinkRodape
import com.brunnakampferd.temvorax.ui.components.TemvoraxTextField
import com.brunnakampferd.temvorax.ui.theme.CremeClaro
import com.brunnakampferd.temvorax.ui.theme.ErroSuave
import com.brunnakampferd.temvorax.ui.theme.RoxoEscuro
import com.brunnakampferd.temvorax.ui.theme.RoxoPrimario
import com.brunnakampferd.temvorax.ui.theme.TextoPrincipal
import com.brunnakampferd.temvorax.ui.theme.TextoSecundario
import com.brunnakampferd.temvorax.viewmodel.AuthUiState
import com.brunnakampferd.temvorax.viewmodel.AuthViewModel
import com.brunnakampferd.temvorax.viewmodel.RecuperacaoSenhaEstado

/**
 * AuthScreen — tela única que alterna entre "Entrar" e "Criar conta", só com
 * e-mail/senha. "Entrar com Google" fica só na tela de boas-vindas
 * ([com.brunnakampferd.temvorax.ui.screens.WelcomeScreen]) — de propósito,
 * pra não duplicar essa opção em dois lugares do fluxo de login.
 *
 * Por que uma tela só (e não duas telas separadas) pra Entrar/Criar conta?
 * Porque os dois modos compartilham quase tudo (campos, validação) — só muda
 * o texto do botão e qual função do ViewModel é chamada. Uma tela só com um
 * "modoLogin" (Boolean) evita duplicar toda essa UI em dois arquivos.
 *
 * @param onAutenticado chamado quando o login/cadastro dá certo, pra navegar pra Home.
 * @param onTermosClick chamado ao clicar em "Termos de Uso".
 * @param onPoliticaClick chamado ao clicar em "Política de Privacidade".
 */
@Composable
fun AuthScreen(
    modoInicial: Boolean = true, // true = abre em "Entrar" | false = abre em "Criar conta"
    onAutenticado: () -> Unit = {},
    onVoltarClick: () -> Unit = {},
    onTermosClick: () -> Unit = {},
    onPoliticaClick: () -> Unit = {},
    viewModel: AuthViewModel = viewModel()
) {
    // true = modo "Entrar" | false = modo "Criar conta"
    var modoLogin by remember { mutableStateOf(modoInicial) }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var mostrarDialogoRecuperacao by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()
    val recuperacaoSenha by viewModel.recuperacaoSenha.collectAsState()

    // Assim que o login/cadastro dá certo, avisa quem está de fora (navegação pra Home).
    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Sucesso) {
            onAutenticado()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CremeClaro)
            .padding(horizontal = 28.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ---- Seta de voltar, no topo, alinhada à esquerda ----
        Row(modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = onVoltarClick) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = TextoPrincipal
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Título e subtítulo direto pela ação, sem mensagem de clima genérica —
        // precisa ficar óbvio se a pessoa está ENTRANDO numa conta que já
        // existe ou CRIANDO uma conta nova, sem depender de reparar em qual
        // botão levou até aqui.
        Text(
            text = if (modoLogin) "Entrar" else "Criar conta",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextoPrincipal
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = if (modoLogin)
                "Entre com sua conta pra continuar de onde parou."
            else
                "Crie sua conta pra começar a usar o Temvorax.",
            fontSize = 14.sp,
            color = TextoPrincipal
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ---- Campos de e-mail e senha ----
        TemvoraxTextField(
            valor = email,
            aoMudar = { email = it },
            rotulo = "E-mail",
            ehEmail = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        TemvoraxTextField(
            valor = senha,
            aoMudar = { senha = it },
            rotulo = "Senha",
            ehSenha = true,
            modifier = Modifier.fillMaxWidth()
        )

        // ---- "Esqueci minha senha" — só faz sentido no modo Entrar, já que no
        // modo Criar conta ainda não existe conta/senha pra recuperar. ----
        if (modoLogin) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                LinkRodape(
                    texto = "Esqueci minha senha",
                    onClick = { mostrarDialogoRecuperacao = true }
                )
            }
        }

        // ---- Mensagem de erro (só aparece se o estado atual for Erro) ----
        val estadoAtual = uiState
        if (estadoAtual is AuthUiState.Erro) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = estadoAtual.mensagem,
                color = ErroSuave,
                fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ---- Botão principal (Entrar OU Criar conta, dependendo do modo) ----
        BotaoSecundario(
            texto = if (modoLogin) "Entrar" else "Criar conta",
            onClick = {
                if (modoLogin) {
                    viewModel.entrarComEmail(email, senha)
                } else {
                    viewModel.criarConta(email, senha)
                }
            }
        )

        // ---- Indicador de carregamento ----
        if (estadoAtual is AuthUiState.Carregando) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator(modifier = Modifier.size(28.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ---- Alternador entre os modos ----
        LinkRodape(
            texto = if (modoLogin) "Ainda não tem conta? Criar conta" else "Já tem conta? Entrar",
            onClick = {
                modoLogin = !modoLogin
                viewModel.limparEstado()
            }
        )

        Spacer(modifier = Modifier.weight(1f))

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

    if (mostrarDialogoRecuperacao) {
        DialogoRecuperarSenha(
            emailInicial = email,
            estado = recuperacaoSenha,
            onEnviar = { viewModel.recuperarSenha(it) },
            onFechar = {
                mostrarDialogoRecuperacao = false
                viewModel.limparRecuperacaoSenha()
            }
        )
    }
}

/**
 * Diálogo de "esqueci minha senha": pede o e-mail (pré-preenchido com o que já
 * estiver digitado no campo principal, pra poupar o usuário de retypar) e manda
 * o link de redefinição via [AuthViewModel.recuperarSenha]. Depois de enviado,
 * mostra uma mensagem de confirmação em vez do campo — não dá pra reenviar sem
 * fechar e abrir o diálogo de novo, de propósito, pra não incentivar spam de e-mails.
 */
@Composable
private fun DialogoRecuperarSenha(
    emailInicial: String,
    estado: RecuperacaoSenhaEstado,
    onEnviar: (String) -> Unit,
    onFechar: () -> Unit
) {
    var email by remember { mutableStateOf(emailInicial) }
    val enviado = estado is RecuperacaoSenhaEstado.Enviado

    AlertDialog(
        onDismissRequest = onFechar,
        title = { Text(if (enviado) "E-mail enviado!" else "Recuperar senha") },
        text = {
            if (enviado) {
                Text(
                    "Se esse e-mail estiver cadastrado no Temvorax, você vai receber um link " +
                        "pra redefinir sua senha em instantes. Não esqueça de checar a caixa de spam.",
                    color = TextoPrincipal
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Digite o e-mail da sua conta pra receber um link de redefinição de senha.",
                        fontSize = 13.sp,
                        color = TextoSecundario
                    )
                    TemvoraxTextField(
                        valor = email,
                        aoMudar = { email = it },
                        rotulo = "E-mail",
                        ehEmail = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (estado is RecuperacaoSenhaEstado.Erro) {
                        Text(estado.mensagem, color = ErroSuave, fontSize = 12.sp)
                    }
                    if (estado is RecuperacaoSenhaEstado.Enviando) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = RoxoPrimario)
                    }
                }
            }
        },
        confirmButton = {
            if (enviado) {
                TextButton(onClick = onFechar) { Text("Fechar") }
            } else {
                TextButton(
                    onClick = { onEnviar(email) },
                    enabled = estado !is RecuperacaoSenhaEstado.Enviando
                ) { Text("Enviar") }
            }
        },
        dismissButton = {
            if (!enviado) {
                TextButton(onClick = onFechar) { Text("Cancelar") }
            }
        }
    )
}