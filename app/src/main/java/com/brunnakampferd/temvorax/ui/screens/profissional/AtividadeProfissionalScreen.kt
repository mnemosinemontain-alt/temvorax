package com.brunnakampferd.temvorax.ui.screens.profissional

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.brunnakampferd.temvorax.data.model.AtividadeProfissional
import com.brunnakampferd.temvorax.ui.components.CartaoItemGenerico
import com.brunnakampferd.temvorax.ui.components.CheckboxComReforco
import com.brunnakampferd.temvorax.ui.components.ListaGenericaScaffold
import com.brunnakampferd.temvorax.ui.components.TelaFormularioScaffold
import com.brunnakampferd.temvorax.ui.components.form.CampoData
import com.brunnakampferd.temvorax.ui.components.form.CampoTexto
import com.brunnakampferd.temvorax.ui.theme.AzulProfissional
import com.brunnakampferd.temvorax.ui.util.formatarCurta
import java.time.LocalDate

@Composable
fun AtividadeProfissionalScreen(
    atividades: List<AtividadeProfissional>,
    onVoltar: () -> Unit,
    onAdicionar: () -> Unit,
    onEditar: (AtividadeProfissional) -> Unit,
    onAlternar: (AtividadeProfissional) -> Unit,
    onExcluir: (AtividadeProfissional) -> Unit,
    modifier: Modifier = Modifier
) {
    ListaGenericaScaffold(
        titulo = "Atividades profissionais",
        itens = atividades.sortedBy { it.data },
        chave = { it.id },
        onVoltar = onVoltar,
        onAdicionar = onAdicionar,
        mensagemVazia = "Nenhuma atividade profissional cadastrada ainda.",
        modifier = modifier
    ) { item ->
        CartaoItemGenerico(
            titulo = item.titulo,
            subtitulo = item.data.formatarCurta(),
            corLateral = AzulProfissional,
            onClick = { onEditar(item) },
            onExcluir = { onExcluir(item) },
            trailing = { CheckboxComReforco(checked = item.concluida, onCheckedChange = { onAlternar(item) }) }
        )
    }
}

@Composable
fun AtividadeProfissionalFormScreen(
    atividade: AtividadeProfissional?,
    onVoltar: () -> Unit,
    onSalvar: (AtividadeProfissional) -> Unit,
    modifier: Modifier = Modifier,
    onExcluir: (() -> Unit)? = null
) {
    var titulo by remember { mutableStateOf(atividade?.titulo ?: "") }
    var descricao by remember { mutableStateOf(atividade?.descricao ?: "") }
    var data by remember { mutableStateOf(atividade?.data ?: LocalDate.now()) }

    TelaFormularioScaffold(
        titulo = if (atividade == null) "Nova atividade" else "Editar atividade",
        onVoltar = onVoltar,
        onExcluir = onExcluir,
        onSalvar = {
            onSalvar(
                (atividade ?: AtividadeProfissional(titulo = titulo, data = data)).copy(
                    titulo = titulo, descricao = descricao, data = data
                )
            )
        },
        modifier = modifier
    ) {
        CampoTexto(titulo, { titulo = it }, "Título da atividade")
        CampoData(data, { it?.let { d -> data = d } }, "Data")
        CampoTexto(descricao, { descricao = it }, "Descrição", linhasMultiplas = true)
    }
}
