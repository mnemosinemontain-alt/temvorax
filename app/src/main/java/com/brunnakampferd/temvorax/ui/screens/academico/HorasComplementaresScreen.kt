package com.brunnakampferd.temvorax.ui.screens.academico

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.brunnakampferd.temvorax.data.model.HoraComplementar
import com.brunnakampferd.temvorax.ui.components.CartaoItemGenerico
import com.brunnakampferd.temvorax.ui.components.ListaGenericaScaffold
import com.brunnakampferd.temvorax.ui.components.TelaFormularioScaffold
import com.brunnakampferd.temvorax.ui.components.form.CampoData
import com.brunnakampferd.temvorax.ui.components.form.CampoNumero
import com.brunnakampferd.temvorax.ui.components.form.CampoTexto
import com.brunnakampferd.temvorax.ui.theme.VerdeAcademico
import com.brunnakampferd.temvorax.ui.util.formatarCurta
import java.time.LocalDate

@Composable
fun HorasComplementaresScreen(
    horas: List<HoraComplementar>,
    onVoltar: () -> Unit,
    onAdicionar: () -> Unit,
    onEditar: (HoraComplementar) -> Unit,
    onExcluir: (HoraComplementar) -> Unit,
    modifier: Modifier = Modifier
) {
    val total = horas.sumOf { it.horas }
    ListaGenericaScaffold(
        titulo = "Horas complementares (total: ${total}h)",
        itens = horas,
        chave = { it.id },
        onVoltar = onVoltar,
        onAdicionar = onAdicionar,
        mensagemVazia = "Nenhuma atividade complementar cadastrada ainda.",
        modifier = modifier
    ) { item ->
        CartaoItemGenerico(
            titulo = item.descricao,
            subtitulo = "${item.horas}h • ${item.data.formatarCurta()}${item.categoria.takeIf { it.isNotBlank() }?.let { " • $it" } ?: ""}",
            corLateral = VerdeAcademico,
            onClick = { onEditar(item) },
            onExcluir = { onExcluir(item) }
        )
    }
}

@Composable
fun HoraComplementarFormScreen(
    item: HoraComplementar?,
    onVoltar: () -> Unit,
    onSalvar: (HoraComplementar) -> Unit,
    modifier: Modifier = Modifier,
    onExcluir: (() -> Unit)? = null
) {
    var descricao by remember { mutableStateOf(item?.descricao ?: "") }
    var categoria by remember { mutableStateOf(item?.categoria ?: "") }
    var horas by remember { mutableStateOf(item?.horas ?: 0.0) }
    var data by remember { mutableStateOf(item?.data ?: LocalDate.now()) }
    var observacoes by remember { mutableStateOf(item?.observacoes ?: "") }

    TelaFormularioScaffold(
        titulo = if (item == null) "Nova atividade complementar" else "Editar atividade complementar",
        onVoltar = onVoltar,
        onExcluir = onExcluir,
        onSalvar = {
            onSalvar(
                (item ?: HoraComplementar(descricao = descricao, horas = horas, data = data)).copy(
                    descricao = descricao, categoria = categoria, horas = horas, data = data, observacoes = observacoes
                )
            )
        },
        modifier = modifier
    ) {
        CampoTexto(descricao, { descricao = it }, "Descrição da atividade")
        CampoTexto(categoria, { categoria = it }, "Categoria (ex.: Curso, Evento, Voluntariado)")
        CampoNumero(horas, { horas = it }, "Quantidade de horas")
        CampoData(data, { it?.let { d -> data = d } }, "Data")
        CampoTexto(observacoes, { observacoes = it }, "Observações", linhasMultiplas = true)
    }
}
