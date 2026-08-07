package com.brunnakampferd.temvorax.ui.screens.pessoal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.brunnakampferd.temvorax.data.model.TarefaDiaria
import com.brunnakampferd.temvorax.ui.components.CartaoItemGenerico
import com.brunnakampferd.temvorax.ui.components.CheckboxComReforco
import com.brunnakampferd.temvorax.ui.components.ListaGenericaScaffold
import com.brunnakampferd.temvorax.ui.components.TelaFormularioScaffold
import com.brunnakampferd.temvorax.ui.components.form.CampoData
import com.brunnakampferd.temvorax.ui.components.form.CampoTexto
import com.brunnakampferd.temvorax.ui.theme.RosaPessoal
import com.brunnakampferd.temvorax.ui.util.formatarCurta
import java.time.LocalDate

@Composable
fun TarefasDiariasScreen(
    tarefas: List<TarefaDiaria>,
    onVoltar: () -> Unit,
    onAdicionar: () -> Unit,
    onAlternar: (TarefaDiaria) -> Unit,
    onExcluir: (TarefaDiaria) -> Unit,
    modifier: Modifier = Modifier
) {
    ListaGenericaScaffold(
        titulo = "Tarefas diárias",
        itens = tarefas.sortedBy { it.data },
        chave = { it.id },
        onVoltar = onVoltar,
        onAdicionar = onAdicionar,
        mensagemVazia = "Nenhuma tarefa diária cadastrada ainda.",
        modifier = modifier
    ) { item ->
        CartaoItemGenerico(
            titulo = item.titulo,
            subtitulo = item.data.formatarCurta(),
            corLateral = RosaPessoal,
            onClick = { onAlternar(item) },
            onExcluir = { onExcluir(item) },
            trailing = { CheckboxComReforco(checked = item.concluida, onCheckedChange = { onAlternar(item) }) }
        )
    }
}

@Composable
fun TarefaDiariaFormScreen(
    tarefa: TarefaDiaria?,
    onVoltar: () -> Unit,
    onSalvar: (TarefaDiaria) -> Unit,
    modifier: Modifier = Modifier,
    onExcluir: (() -> Unit)? = null
) {
    var titulo by remember { mutableStateOf(tarefa?.titulo ?: "") }
    var data by remember { mutableStateOf(tarefa?.data ?: LocalDate.now()) }

    TelaFormularioScaffold(
        titulo = if (tarefa == null) "Nova tarefa diária" else "Editar tarefa diária",
        onVoltar = onVoltar,
        onExcluir = onExcluir,
        onSalvar = {
            onSalvar((tarefa ?: TarefaDiaria(titulo = titulo, data = data)).copy(titulo = titulo, data = data))
        },
        modifier = modifier
    ) {
        CampoTexto(titulo, { titulo = it }, "O que você precisa fazer?")
        CampoData(data, { it?.let { d -> data = d } }, "Data")
    }
}
