package com.brunnakampferd.temvorax.ui.screens.academico

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.brunnakampferd.temvorax.data.model.AnexoTcc
import com.brunnakampferd.temvorax.data.model.EtapaTcc
import com.brunnakampferd.temvorax.data.model.Tcc
import com.brunnakampferd.temvorax.ui.components.CartaoItemGenerico
import com.brunnakampferd.temvorax.ui.components.ListaGenericaScaffold
import com.brunnakampferd.temvorax.ui.components.TelaFormularioScaffold
import com.brunnakampferd.temvorax.ui.components.form.CampoData
import com.brunnakampferd.temvorax.ui.components.form.CampoSelecao
import com.brunnakampferd.temvorax.ui.components.form.CampoTexto
import com.brunnakampferd.temvorax.ui.theme.ErroSuave
import com.brunnakampferd.temvorax.ui.theme.RoxoPrimario
import com.brunnakampferd.temvorax.ui.theme.TextoPrincipal
import com.brunnakampferd.temvorax.ui.theme.TextoSecundario
import com.brunnakampferd.temvorax.ui.theme.VerdeAcademico
import com.brunnakampferd.temvorax.viewmodel.EnvioAnexoEstado

@Composable
fun TccScreen(
    tccs: List<Tcc>,
    onVoltar: () -> Unit,
    onAdicionar: () -> Unit,
    onEditar: (Tcc) -> Unit,
    onExcluir: (Tcc) -> Unit,
    modifier: Modifier = Modifier
) {
    ListaGenericaScaffold(
        titulo = "TCC",
        itens = tccs,
        chave = { it.id },
        onVoltar = onVoltar,
        onAdicionar = onAdicionar,
        mensagemVazia = "Nenhum TCC cadastrado ainda.",
        modifier = modifier
    ) { tcc ->
        CartaoItemGenerico(
            titulo = tcc.titulo,
            subtitulo = tcc.etapaAtual.rotulo,
            corLateral = VerdeAcademico,
            onClick = { onEditar(tcc) },
            onExcluir = { onExcluir(tcc) }
        )
    }
}

/**
 * @param anexos PDFs já anexados a este TCC — só faz sentido (e só aparece a seção)
 *   quando [tcc] já existe, já que anexo precisa de um id de TCC salvo pra se vincular.
 * @param envioAnexo estado do upload em andamento, pra mostrar loading/erro na seção de anexos.
 * @param onAnexarArquivo chamado com o Uri escolhido no seletor de arquivos e o nome de exibição dele.
 */
@Composable
fun TccFormScreen(
    tcc: Tcc?,
    onVoltar: () -> Unit,
    onSalvar: (Tcc) -> Unit,
    modifier: Modifier = Modifier,
    onExcluir: (() -> Unit)? = null,
    anexos: List<AnexoTcc> = emptyList(),
    envioAnexo: EnvioAnexoEstado = EnvioAnexoEstado.Ocioso,
    onAnexarArquivo: (Uri, String) -> Unit = { _, _ -> },
    onExcluirAnexo: (AnexoTcc) -> Unit = {}
) {
    var titulo by remember { mutableStateOf(tcc?.titulo ?: "") }
    var orientador by remember { mutableStateOf(tcc?.orientador ?: "") }
    var dataInicio by remember { mutableStateOf(tcc?.dataInicio) }
    var dataDefesa by remember { mutableStateOf(tcc?.dataPrevistaDefesa) }
    var etapa by remember { mutableStateOf(tcc?.etapaAtual ?: EtapaTcc.TEMA_DEFINIDO) }
    var observacoes by remember { mutableStateOf(tcc?.observacoes ?: "") }

    val contexto = LocalContext.current
    val seletorArquivo = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> if (uri != null) onAnexarArquivo(uri, nomeArquivoDoUri(contexto, uri)) }

    TelaFormularioScaffold(
        titulo = if (tcc == null) "Novo TCC" else "Editar TCC",
        onVoltar = onVoltar,
        onExcluir = onExcluir,
        onSalvar = {
            onSalvar(
                (tcc ?: Tcc(titulo = titulo)).copy(
                    titulo = titulo,
                    orientador = orientador,
                    dataInicio = dataInicio,
                    dataPrevistaDefesa = dataDefesa,
                    etapaAtual = etapa,
                    observacoes = observacoes
                )
            )
        },
        modifier = modifier
    ) {
        CampoTexto(titulo, { titulo = it }, "Título do trabalho (pode ser provisório)")
        CampoTexto(orientador, { orientador = it }, "Orientador(a)")
        CampoData(dataInicio, { dataInicio = it }, "Data de início")
        CampoData(dataDefesa, { dataDefesa = it }, "Data prevista de defesa/entrega")
        CampoSelecao(etapa, EtapaTcc.entries, { it.rotulo }, "Etapa atual", { etapa = it })
        CampoTexto(observacoes, { observacoes = it }, "Observações", linhasMultiplas = true)

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        if (tcc == null) {
            Text(
                text = "Salve o TCC primeiro pra poder anexar arquivos (pesquisas, referências, versões do texto...).",
                color = TextoSecundario,
                fontSize = 13.sp
            )
        } else {
            SecaoAnexos(
                anexos = anexos,
                envioAnexo = envioAnexo,
                onAdicionar = { seletorArquivo.launch("application/pdf") },
                onAbrir = { anexo -> contexto.startActivity(Intent(Intent.ACTION_VIEW, anexo.url.toUri())) },
                onExcluir = onExcluirAnexo
            )
        }
    }
}

@Composable
private fun SecaoAnexos(
    anexos: List<AnexoTcc>,
    envioAnexo: EnvioAnexoEstado,
    onAdicionar: () -> Unit,
    onAbrir: (AnexoTcc) -> Unit,
    onExcluir: (AnexoTcc) -> Unit
) {
    Column {
        Text(text = "Anexos (PDF)", fontWeight = FontWeight.SemiBold, color = TextoPrincipal, fontSize = 15.sp)
        Spacer(Modifier.height(8.dp))

        anexos.forEach { anexo ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.InsertDriveFile, contentDescription = null, tint = VerdeAcademico)
                Text(
                    text = anexo.nomeArquivo,
                    color = TextoPrincipal,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f).padding(horizontal = 10.dp).clickable { onAbrir(anexo) }
                )
                IconButton(onClick = { onExcluir(anexo) }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Excluir anexo", tint = ErroSuave)
                }
            }
        }

        if (envioAnexo is EnvioAnexoEstado.Erro) {
            Text(text = envioAnexo.mensagem, color = ErroSuave, fontSize = 12.sp, modifier = Modifier.padding(vertical = 4.dp))
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(onClick = onAdicionar, enabled = envioAnexo !is EnvioAnexoEstado.Enviando) {
                Icon(Icons.Filled.AttachFile, contentDescription = null, tint = RoxoPrimario)
                Spacer(Modifier.width(8.dp))
                Text(if (envioAnexo is EnvioAnexoEstado.Enviando) "Enviando..." else "Anexar PDF")
            }
            if (envioAnexo is EnvioAnexoEstado.Enviando) {
                Spacer(Modifier.width(12.dp))
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = RoxoPrimario, strokeWidth = 2.dp)
            }
        }
    }
}

/** Lê o nome "de exibição" do arquivo escolhido no seletor — cai pra um nome genérico se o provider não informar. */
private fun nomeArquivoDoUri(contexto: Context, uri: Uri): String {
    contexto.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val indiceNome = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (indiceNome != -1 && cursor.moveToFirst()) {
            cursor.getString(indiceNome)?.let { return it }
        }
    }
    return "documento.pdf"
}
