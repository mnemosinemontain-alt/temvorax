package com.brunnakampferd.temvorax.ui.screens.academico

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.brunnakampferd.temvorax.data.model.Semestre
import com.brunnakampferd.temvorax.ui.components.CartaoItemGenerico
import com.brunnakampferd.temvorax.ui.components.ListaGenericaScaffold
import com.brunnakampferd.temvorax.ui.components.TelaFormularioScaffold
import com.brunnakampferd.temvorax.ui.components.form.CampoBooleano
import com.brunnakampferd.temvorax.ui.components.form.CampoData
import com.brunnakampferd.temvorax.ui.components.form.CampoTexto
import com.brunnakampferd.temvorax.ui.theme.TealPrimario
import com.brunnakampferd.temvorax.ui.theme.TextoSecundario

@Composable
fun SemestresScreen(
    semestres: List<Semestre>,
    onVoltar: () -> Unit,
    onAdicionar: () -> Unit,
    onEditar: (Semestre) -> Unit,
    onExcluir: (Semestre) -> Unit,
    onAbrirDetalhe: (Semestre) -> Unit,
    modifier: Modifier = Modifier
) {
    ListaGenericaScaffold(
        titulo = "Semestres",
        itens = semestres,
        chave = { it.id },
        onVoltar = onVoltar,
        onAdicionar = onAdicionar,
        mensagemVazia = "Nenhum semestre cadastrado ainda. Toque em + pra criar (ex.: \"2026.1\").",
        modifier = modifier
    ) { semestre ->
        CartaoItemGenerico(
            titulo = semestre.nome,
            subtitulo = if (semestre.ativo) "Ativo" else null,
            corLateral = if (semestre.ativo) TealPrimario else TextoSecundario,
            onClick = { onAbrirDetalhe(semestre) },
            onExcluir = { onExcluir(semestre) },
            trailing = {
                IconButton(onClick = { onEditar(semestre) }) {
                    Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = TextoSecundario)
                }
            }
        )
    }
}

@Composable
fun SemestreFormScreen(
    semestre: Semestre?,
    onVoltar: () -> Unit,
    onSalvar: (Semestre) -> Unit,
    modifier: Modifier = Modifier,
    onExcluir: (() -> Unit)? = null
) {
    var nome by remember { mutableStateOf(semestre?.nome ?: "") }
    var dataInicio by remember { mutableStateOf(semestre?.dataInicio) }
    var dataFim by remember { mutableStateOf(semestre?.dataFim) }
    var ativo by remember { mutableStateOf(semestre?.ativo ?: true) }

    TelaFormularioScaffold(
        titulo = if (semestre == null) "Novo semestre" else "Editar semestre",
        onVoltar = onVoltar,
        onExcluir = onExcluir,
        onSalvar = {
            onSalvar(
                (semestre ?: Semestre(nome = nome)).copy(
                    nome = nome,
                    dataInicio = dataInicio,
                    dataFim = dataFim,
                    ativo = ativo
                )
            )
        },
        modifier = modifier
    ) {
        CampoTexto(nome, { nome = it }, "Nome do semestre (ex.: 2026.1)")
        CampoData(dataInicio, { dataInicio = it }, "Início")
        CampoData(dataFim, { dataFim = it }, "Fim previsto")
        CampoBooleano(ativo, { ativo = it }, "Semestre ativo agora")
    }
}
