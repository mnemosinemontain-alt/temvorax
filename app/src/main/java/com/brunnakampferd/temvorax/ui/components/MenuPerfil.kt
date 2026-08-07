package com.brunnakampferd.temvorax.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.brunnakampferd.temvorax.data.model.NivelEnsino
import com.brunnakampferd.temvorax.data.model.Perfil
import com.brunnakampferd.temvorax.data.model.Turno
import com.brunnakampferd.temvorax.ui.components.form.CampoData
import com.brunnakampferd.temvorax.ui.components.form.CampoSelecao
import com.brunnakampferd.temvorax.ui.components.form.CampoTexto
import com.brunnakampferd.temvorax.ui.theme.CremeClaro
import com.brunnakampferd.temvorax.ui.theme.ErroSuave
import com.brunnakampferd.temvorax.ui.theme.LavandaClaro
import com.brunnakampferd.temvorax.ui.theme.RoxoPrimario
import com.brunnakampferd.temvorax.ui.theme.TextoPrincipal
import com.brunnakampferd.temvorax.ui.theme.TextoSecundario
import com.brunnakampferd.temvorax.viewmodel.EnvioFotoEstado

/**
 * Conteúdo do menu lateral de Perfil (drawer da direita): foto, dados
 * profissionais e acadêmicos, e as ações de conta (Termos, Privacidade,
 * Sair, Excluir conta).
 *
 * Mantém uma cópia local editável (`estadoLocal`) dos dados — só grava no
 * banco quando o usuário toca em "Salvar perfil", pra não escrever no banco
 * a cada letra digitada.
 *
 * @param onExcluirConta chamado quando o usuário CONFIRMA a exclusão no
 *   diálogo. Por enquanto isso é só uma confirmação visual — a exclusão de
 *   verdade (Firebase + dados locais) fica pra quando o app tiver um fluxo
 *   de reautenticação, já que o Firebase exige login recente pra apagar conta.
 */
@Composable
fun MenuPerfil(
    perfil: Perfil,
    envioFoto: EnvioFotoEstado,
    onEscolherFoto: (Uri) -> Unit,
    onSalvar: (Perfil) -> Unit,
    onTermosClick: () -> Unit,
    onPoliticaClick: () -> Unit,
    onSairClick: () -> Unit,
    onExcluirConta: () -> Unit,
    modifier: Modifier = Modifier
) {
    var estadoLocal by remember(perfil) { mutableStateOf(perfil) }
    var mostrarConfirmarExclusao by remember { mutableStateOf(false) }
    // Reseta sempre que a URL muda, pra uma foto nova ter chance de carregar
    // mesmo que a anterior tenha falhado (ex.: objeto apagado fora do app).
    var falhaAoCarregarFoto by remember(estadoLocal.fotoUrl) { mutableStateOf(false) }

    val seletorFoto = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null) onEscolherFoto(uri) }

    ModalDrawerSheet(modifier = modifier, drawerContainerColor = CremeClaro) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // ---- Foto de perfil ----
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    // Cai pro ícone padrão tanto quando não tem foto cadastrada quanto
                    // quando a URL salva não carrega mais (evita mostrar um erro/ícone
                    // quebrado — ver PerfilRepository.enviarFotoPerfil).
                    if (estadoLocal.fotoUrl != null && !falhaAoCarregarFoto) {
                        AsyncImage(
                            model = estadoLocal.fotoUrl,
                            contentDescription = "Foto de perfil",
                            contentScale = ContentScale.Crop,
                            onError = { falhaAoCarregarFoto = true },
                            modifier = Modifier.size(88.dp).clip(CircleShape)
                        )
                    } else {
                        Box(
                            modifier = Modifier.size(88.dp).clip(CircleShape).background(LavandaClaro),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = null,
                                tint = RoxoPrimario,
                                modifier = Modifier.size(44.dp)
                            )
                        }
                    }
                    if (envioFoto is EnvioFotoEstado.Enviando) {
                        CircularProgressIndicator(modifier = Modifier.size(88.dp), color = RoxoPrimario)
                    }
                    IconButton(
                        onClick = {
                            seletorFoto.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .background(RoxoPrimario, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PhotoCamera,
                            contentDescription = "Trocar foto",
                            tint = CremeClaro,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            if (envioFoto is EnvioFotoEstado.Erro) {
                Text(
                    text = envioFoto.mensagem,
                    color = ErroSuave,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            // ---- Profissional ----
            Text("Profissional", fontWeight = FontWeight.Bold, color = TextoPrincipal)
            Spacer(Modifier.height(8.dp))
            CampoTexto(estadoLocal.profissao, { estadoLocal = estadoLocal.copy(profissao = it) }, "Profissão")
            Spacer(Modifier.height(10.dp))
            CampoTexto(estadoLocal.empresa, { estadoLocal = estadoLocal.copy(empresa = it) }, "Empresa")
            Spacer(Modifier.height(10.dp))
            CampoTexto(estadoLocal.cargo, { estadoLocal = estadoLocal.copy(cargo = it) }, "Cargo")

            Spacer(Modifier.height(20.dp))

            // ---- Acadêmica ----
            Text("Acadêmica", fontWeight = FontWeight.Bold, color = TextoPrincipal)
            Spacer(Modifier.height(8.dp))
            CampoTexto(estadoLocal.nomeCompleto, { estadoLocal = estadoLocal.copy(nomeCompleto = it) }, "Nome completo")
            Spacer(Modifier.height(10.dp))
            CampoData(estadoLocal.dataNascimento, { estadoLocal = estadoLocal.copy(dataNascimento = it) }, "Data de nascimento")
            Spacer(Modifier.height(10.dp))
            CampoTexto(estadoLocal.instituicaoEnsino, { estadoLocal = estadoLocal.copy(instituicaoEnsino = it) }, "Instituição de ensino")
            Spacer(Modifier.height(10.dp))
            CampoTexto(estadoLocal.curso, { estadoLocal = estadoLocal.copy(curso = it) }, "Curso")
            Spacer(Modifier.height(10.dp))
            CampoSelecao(
                valor = estadoLocal.nivelEnsino,
                opcoes = NivelEnsino.entries,
                rotuloOpcao = { it.rotulo },
                rotulo = "Nível de ensino",
                aoMudar = { estadoLocal = estadoLocal.copy(nivelEnsino = it) }
            )
            Spacer(Modifier.height(10.dp))
            CampoTexto(estadoLocal.semestrePeriodo, { estadoLocal = estadoLocal.copy(semestrePeriodo = it) }, "Semestre/período")
            Spacer(Modifier.height(10.dp))
            CampoSelecao(
                valor = estadoLocal.turno,
                opcoes = Turno.entries,
                rotuloOpcao = { it.rotulo },
                rotulo = "Turno",
                aoMudar = { estadoLocal = estadoLocal.copy(turno = it) }
            )
            Spacer(Modifier.height(10.dp))
            CampoTexto(estadoLocal.cidade, { estadoLocal = estadoLocal.copy(cidade = it) }, "Cidade")

            Spacer(Modifier.height(16.dp))
            BotaoPrimario(texto = "Salvar perfil", onClick = { onSalvar(estadoLocal) })

            Spacer(Modifier.height(20.dp))
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))

            LinkRodape(texto = "Termos de Uso", onClick = onTermosClick)
            LinkRodape(texto = "Política de Privacidade", onClick = onPoliticaClick)

            Spacer(Modifier.height(8.dp))

            TextButton(onClick = onSairClick, modifier = Modifier.fillMaxWidth()) {
                Text("Sair da conta", color = TextoPrincipal)
            }
            TextButton(onClick = { mostrarConfirmarExclusao = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Excluir minha conta e meus dados", color = ErroSuave, fontSize = 13.sp)
            }
        }
    }

    if (mostrarConfirmarExclusao) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmarExclusao = false },
            title = { Text("Excluir conta e dados?") },
            text = {
                Text(
                    "Isso apagaria sua conta e tudo que você cadastrou no Temvorax, de forma irreversível. " +
                        "Essa exclusão de verdade ainda não está pronta neste app (precisa de um fluxo de " +
                        "reautenticação antes) — por enquanto essa confirmação não apaga nada.",
                    color = TextoSecundario,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    mostrarConfirmarExclusao = false
                    onExcluirConta()
                }) { Text("Entendi", color = ErroSuave) }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmarExclusao = false }) { Text("Cancelar") }
            }
        )
    }
}
