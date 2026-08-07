package com.brunnakampferd.temvorax.ui.screens.academico

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.brunnakampferd.temvorax.data.model.Disciplina
import com.brunnakampferd.temvorax.data.model.Prova
import com.brunnakampferd.temvorax.ui.components.TelaFormularioScaffold
import com.brunnakampferd.temvorax.ui.components.form.CampoData
import com.brunnakampferd.temvorax.ui.components.form.CampoSelecao
import com.brunnakampferd.temvorax.ui.components.form.CampoTexto
import java.time.LocalDate

@Composable
fun ProvaFormScreen(
    semestreId: Long,
    prova: Prova?,
    disciplinasDoSemestre: List<Disciplina>,
    onVoltar: () -> Unit,
    onSalvar: (Prova) -> Unit,
    modifier: Modifier = Modifier,
    onExcluir: (() -> Unit)? = null
) {
    var titulo by remember { mutableStateOf(prova?.titulo ?: "") }
    var data by remember { mutableStateOf(prova?.data ?: LocalDate.now()) }
    var disciplina by remember { mutableStateOf(disciplinasDoSemestre.find { it.id == prova?.disciplinaId }) }
    var observacoes by remember { mutableStateOf(prova?.observacoes ?: "") }

    TelaFormularioScaffold(
        titulo = if (prova == null) "Nova prova" else "Editar prova",
        onVoltar = onVoltar,
        onExcluir = onExcluir,
        onSalvar = {
            onSalvar(
                (prova ?: Prova(semestreId = semestreId, titulo = titulo, data = data)).copy(
                    titulo = titulo,
                    data = data,
                    disciplinaId = disciplina?.id,
                    observacoes = observacoes
                )
            )
        },
        modifier = modifier
    ) {
        CampoTexto(titulo, { titulo = it }, "Título da prova")
        CampoData(data, { it?.let { d -> data = d } }, "Data")
        if (disciplinasDoSemestre.isNotEmpty()) {
            CampoSelecao(
                valor = disciplina,
                opcoes = disciplinasDoSemestre,
                rotuloOpcao = { it.nome },
                rotulo = "Disciplina",
                aoMudar = { disciplina = it }
            )
        }
        CampoTexto(observacoes, { observacoes = it }, "Observações", linhasMultiplas = true)
    }
}
