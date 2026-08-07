package com.brunnakampferd.temvorax.ui.screens.academico

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.brunnakampferd.temvorax.data.model.Disciplina
import com.brunnakampferd.temvorax.data.model.TrabalhoAcademico
import com.brunnakampferd.temvorax.ui.components.TelaFormularioScaffold
import com.brunnakampferd.temvorax.ui.components.form.CampoBooleano
import com.brunnakampferd.temvorax.ui.components.form.CampoData
import com.brunnakampferd.temvorax.ui.components.form.CampoSelecao
import com.brunnakampferd.temvorax.ui.components.form.CampoTexto
import java.time.LocalDate

@Composable
fun TrabalhoFormScreen(
    semestreId: Long,
    trabalho: TrabalhoAcademico?,
    disciplinasDoSemestre: List<Disciplina>,
    onVoltar: () -> Unit,
    onSalvar: (TrabalhoAcademico) -> Unit,
    modifier: Modifier = Modifier,
    onExcluir: (() -> Unit)? = null
) {
    var titulo by remember { mutableStateOf(trabalho?.titulo ?: "") }
    var dataEntrega by remember { mutableStateOf(trabalho?.dataEntrega ?: LocalDate.now()) }
    var disciplina by remember { mutableStateOf(disciplinasDoSemestre.find { it.id == trabalho?.disciplinaId }) }
    var concluido by remember { mutableStateOf(trabalho?.concluido ?: false) }
    var observacoes by remember { mutableStateOf(trabalho?.observacoes ?: "") }

    TelaFormularioScaffold(
        titulo = if (trabalho == null) "Novo trabalho" else "Editar trabalho",
        onVoltar = onVoltar,
        onExcluir = onExcluir,
        onSalvar = {
            onSalvar(
                (trabalho ?: TrabalhoAcademico(semestreId = semestreId, titulo = titulo, dataEntrega = dataEntrega)).copy(
                    titulo = titulo,
                    dataEntrega = dataEntrega,
                    disciplinaId = disciplina?.id,
                    concluido = concluido,
                    observacoes = observacoes
                )
            )
        },
        modifier = modifier
    ) {
        CampoTexto(titulo, { titulo = it }, "Título do trabalho")
        CampoData(dataEntrega, { it?.let { d -> dataEntrega = d } }, "Data de entrega")
        if (disciplinasDoSemestre.isNotEmpty()) {
            CampoSelecao(
                valor = disciplina,
                opcoes = disciplinasDoSemestre,
                rotuloOpcao = { it.nome },
                rotulo = "Disciplina",
                aoMudar = { disciplina = it }
            )
        }
        CampoBooleano(concluido, { concluido = it }, "Já entreguei")
        CampoTexto(observacoes, { observacoes = it }, "Observações", linhasMultiplas = true)
    }
}
