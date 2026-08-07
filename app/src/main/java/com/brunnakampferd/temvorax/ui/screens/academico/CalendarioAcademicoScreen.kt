package com.brunnakampferd.temvorax.ui.screens.academico

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.brunnakampferd.temvorax.data.calendario.CalendarioDispositivo
import com.brunnakampferd.temvorax.data.model.EventoCalendarioAcademico
import com.brunnakampferd.temvorax.ui.components.CartaoItemGenerico
import com.brunnakampferd.temvorax.ui.components.ListaGenericaScaffold
import com.brunnakampferd.temvorax.ui.components.TelaFormularioScaffold
import com.brunnakampferd.temvorax.ui.components.form.CampoData
import com.brunnakampferd.temvorax.ui.components.form.CampoTexto
import com.brunnakampferd.temvorax.ui.theme.AzulProfissional
import com.brunnakampferd.temvorax.ui.util.formatarCurta
import com.brunnakampferd.temvorax.viewmodel.SincronizacaoCalendarioEstado
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * Datas oficiais da instituição (início/fim de período, feriados, semana de
 * provas...) — informativo, fixo, não muda com o semestre que o usuário
 * escolheu como ativo (diferente da lista de Semestres).
 *
 * @param sincronizacao estado da sincronização com o Calendário nativo do Android em andamento
 * @param onObterCalendarios lista os calendários graviáveis do dispositivo (pede pra rodar em IO)
 * @param onSincronizar dispara a sincronização de todas as [eventos] pro calendário escolhido
 * @param onSincronizacaoConsumida chamado ao fechar o diálogo de resultado, pra voltar ao estado ocioso
 */
@Composable
fun CalendarioAcademicoScreen(
    eventos: List<EventoCalendarioAcademico>,
    onVoltar: () -> Unit,
    onAdicionar: () -> Unit,
    onEditar: (EventoCalendarioAcademico) -> Unit,
    onExcluir: (EventoCalendarioAcademico) -> Unit,
    modifier: Modifier = Modifier,
    sincronizacao: SincronizacaoCalendarioEstado = SincronizacaoCalendarioEstado.Ociosa,
    onObterCalendarios: suspend (Context) -> List<CalendarioDispositivo.CalendarioDisponivel> = { emptyList() },
    onSincronizar: (Context, Long) -> Unit = { _, _ -> },
    onSincronizacaoConsumida: () -> Unit = {}
) {
    val contexto = LocalContext.current
    val escopo = rememberCoroutineScope()

    var calendariosParaEscolher by remember { mutableStateOf<List<CalendarioDispositivo.CalendarioDisponivel>>(emptyList()) }
    var mostrarSemCalendario by remember { mutableStateOf(false) }
    var mostrarPermissaoNegada by remember { mutableStateOf(false) }

    fun iniciarSincronizacao() {
        escopo.launch {
            val calendarios = onObterCalendarios(contexto)
            when {
                calendarios.isEmpty() -> mostrarSemCalendario = true
                calendarios.size == 1 -> onSincronizar(contexto, calendarios.first().id)
                else -> calendariosParaEscolher = calendarios
            }
        }
    }

    val seletorPermissoes = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { resultado ->
        if (resultado.values.all { it }) iniciarSincronizacao() else mostrarPermissaoNegada = true
    }

    ListaGenericaScaffold(
        titulo = "Calendário acadêmico",
        itens = eventos.sortedBy { it.data },
        chave = { it.id },
        onVoltar = onVoltar,
        onAdicionar = onAdicionar,
        mensagemVazia = "Nenhuma data cadastrada ainda. Adicione as datas oficiais da sua instituição.",
        acaoTopo = {
            IconButton(
                onClick = {
                    if (temPermissaoCalendario(contexto)) {
                        iniciarSincronizacao()
                    } else {
                        seletorPermissoes.launch(
                            arrayOf(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR)
                        )
                    }
                },
                enabled = sincronizacao !is SincronizacaoCalendarioEstado.Sincronizando
            ) {
                Icon(Icons.Filled.Sync, contentDescription = "Sincronizar com o calendário do celular", tint = AzulProfissional)
            }
        },
        modifier = modifier
    ) { evento ->
        CartaoItemGenerico(
            titulo = evento.titulo,
            subtitulo = evento.data.formatarCurta(),
            corLateral = AzulProfissional,
            onClick = { onEditar(evento) },
            onExcluir = { onExcluir(evento) }
        )
    }

    if (calendariosParaEscolher.isNotEmpty()) {
        DialogoEscolherCalendario(
            calendarios = calendariosParaEscolher,
            onEscolher = { calendarioId ->
                calendariosParaEscolher = emptyList()
                onSincronizar(contexto, calendarioId)
            },
            onCancelar = { calendariosParaEscolher = emptyList() }
        )
    }

    if (mostrarSemCalendario) {
        DialogoAviso(
            titulo = "Nenhum calendário disponível",
            mensagem = "Não encontrei nenhum calendário no celular pra sincronizar. Adicione uma conta Google no aparelho (Configurações > Contas) e tenta de novo.",
            onFechar = { mostrarSemCalendario = false }
        )
    }

    if (mostrarPermissaoNegada) {
        DialogoAviso(
            titulo = "Permissão necessária",
            mensagem = "Pra sincronizar, o Temvorax precisa de acesso ao Calendário do celular. Você pode conceder essa permissão nas configurações do app.",
            onFechar = { mostrarPermissaoNegada = false }
        )
    }

    when (val estado = sincronizacao) {
        is SincronizacaoCalendarioEstado.Concluida -> DialogoAviso(
            titulo = "Sincronizado!",
            mensagem = if (estado.quantidade == 0) {
                "Não tinha nenhuma data cadastrada ainda pra sincronizar."
            } else {
                "${estado.quantidade} ${if (estado.quantidade == 1) "data foi sincronizada" else "datas foram sincronizadas"} com o calendário do celular."
            },
            onFechar = onSincronizacaoConsumida
        )
        is SincronizacaoCalendarioEstado.Erro -> DialogoAviso(
            titulo = "Não foi possível sincronizar",
            mensagem = estado.mensagem,
            onFechar = onSincronizacaoConsumida
        )
        else -> Unit
    }
}

private fun temPermissaoCalendario(context: Context): Boolean =
    ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED

@Composable
private fun DialogoEscolherCalendario(
    calendarios: List<CalendarioDispositivo.CalendarioDisponivel>,
    onEscolher: (Long) -> Unit,
    onCancelar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Sincronizar com qual calendário?") },
        text = {
            Column {
                calendarios.forEach { calendarioItem ->
                    Row(modifier = Modifier.padding(vertical = 4.dp)) {
                        RadioButton(selected = false, onClick = { onEscolher(calendarioItem.id) })
                        Column {
                            Text(calendarioItem.nomeExibicao)
                            Text(calendarioItem.nomeConta, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}

@Composable
private fun DialogoAviso(titulo: String, mensagem: String, onFechar: () -> Unit) {
    AlertDialog(
        onDismissRequest = onFechar,
        title = { Text(titulo) },
        text = { Text(mensagem) },
        confirmButton = { TextButton(onClick = onFechar) { Text("OK") } }
    )
}

@Composable
fun EventoCalendarioFormScreen(
    evento: EventoCalendarioAcademico?,
    onVoltar: () -> Unit,
    onSalvar: (EventoCalendarioAcademico) -> Unit,
    modifier: Modifier = Modifier,
    onExcluir: (() -> Unit)? = null
) {
    var titulo by remember { mutableStateOf(evento?.titulo ?: "") }
    var data by remember { mutableStateOf(evento?.data ?: LocalDate.now()) }
    var observacoes by remember { mutableStateOf(evento?.observacoes ?: "") }

    TelaFormularioScaffold(
        titulo = if (evento == null) "Nova data" else "Editar data",
        onVoltar = onVoltar,
        onExcluir = onExcluir,
        onSalvar = {
            onSalvar(
                (evento ?: EventoCalendarioAcademico(titulo = titulo, data = data)).copy(
                    titulo = titulo, data = data, observacoes = observacoes
                )
            )
        },
        modifier = modifier
    ) {
        CampoTexto(titulo, { titulo = it }, "Título (ex.: Início do período)")
        CampoData(data, { it?.let { d -> data = d } }, "Data")
        CampoTexto(observacoes, { observacoes = it }, "Observações", linhasMultiplas = true)
    }
}
