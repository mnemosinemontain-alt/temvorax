package com.brunnakampferd.temvorax.ui.screens.profissional

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.brunnakampferd.temvorax.data.model.CompromissoProfissional
import com.brunnakampferd.temvorax.ui.components.CartaoItemGenerico
import com.brunnakampferd.temvorax.ui.components.ListaGenericaScaffold
import com.brunnakampferd.temvorax.ui.components.TelaFormularioScaffold
import com.brunnakampferd.temvorax.ui.components.form.CampoBooleano
import com.brunnakampferd.temvorax.ui.components.form.CampoData
import com.brunnakampferd.temvorax.ui.components.form.CampoHora
import com.brunnakampferd.temvorax.ui.components.form.CampoMultiSelecaoDias
import com.brunnakampferd.temvorax.ui.components.form.CampoTexto
import com.brunnakampferd.temvorax.ui.theme.AzulProfissional
import com.brunnakampferd.temvorax.ui.util.subtituloCompromisso
import java.time.LocalDate

@Composable
fun CompromissoProfissionalScreen(
    compromissos: List<CompromissoProfissional>,
    onVoltar: () -> Unit,
    onAdicionar: () -> Unit,
    onEditar: (CompromissoProfissional) -> Unit,
    onExcluir: (CompromissoProfissional) -> Unit,
    modifier: Modifier = Modifier
) {
    ListaGenericaScaffold(
        titulo = "Compromissos profissionais",
        itens = compromissos.sortedBy { it.data },
        chave = { it.id },
        onVoltar = onVoltar,
        onAdicionar = onAdicionar,
        mensagemVazia = "Nenhum compromisso profissional cadastrado ainda.",
        modifier = modifier
    ) { item ->
        CartaoItemGenerico(
            titulo = item.titulo,
            subtitulo = subtituloCompromisso(item.diasSemana, item.data, item.horaInicio),
            corLateral = AzulProfissional,
            onClick = { onEditar(item) },
            onExcluir = { onExcluir(item) }
        )
    }
}

@Composable
fun CompromissoProfissionalFormScreen(
    compromisso: CompromissoProfissional?,
    onVoltar: () -> Unit,
    onSalvar: (CompromissoProfissional) -> Unit,
    modifier: Modifier = Modifier,
    onExcluir: (() -> Unit)? = null
) {
    var titulo by remember { mutableStateOf(compromisso?.titulo ?: "") }
    var data by remember { mutableStateOf(compromisso?.data ?: LocalDate.now()) }
    var horaInicio by remember { mutableStateOf(compromisso?.horaInicio) }
    var horaFim by remember { mutableStateOf(compromisso?.horaFim) }
    var local by remember { mutableStateOf(compromisso?.local ?: "") }
    var observacoes by remember { mutableStateOf(compromisso?.observacoes ?: "") }
    var recorrente by remember { mutableStateOf(!compromisso?.diasSemana.isNullOrEmpty()) }
    var diasSemana by remember { mutableStateOf(compromisso?.diasSemana ?: emptySet()) }
    var recorrenteAte by remember { mutableStateOf(compromisso?.recorrenteAte) }

    TelaFormularioScaffold(
        titulo = if (compromisso == null) "Novo compromisso" else "Editar compromisso",
        onVoltar = onVoltar,
        onExcluir = onExcluir,
        onSalvar = {
            onSalvar(
                (compromisso ?: CompromissoProfissional(titulo = titulo, data = data)).copy(
                    titulo = titulo,
                    data = data,
                    horaInicio = horaInicio,
                    horaFim = horaFim,
                    local = local,
                    observacoes = observacoes,
                    diasSemana = if (recorrente) diasSemana else null,
                    recorrenteAte = if (recorrente) recorrenteAte else null
                )
            )
        },
        modifier = modifier
    ) {
        CampoTexto(titulo, { titulo = it }, "Título do compromisso")
        CampoBooleano(
            valor = recorrente,
            aoMudar = { recorrente = it },
            rotulo = "Compromisso recorrente"
        )
        CampoData(data, { it?.let { d -> data = d } }, if (recorrente) "A partir de" else "Data")
        if (recorrente) {
            CampoMultiSelecaoDias(diasSemana, { diasSemana = it }, "Dias da semana")
            CampoData(recorrenteAte, { recorrenteAte = it }, "Até quando (deixe em branco pra continuar indefinidamente)")
        }
        CampoHora(horaInicio, { horaInicio = it }, "Início")
        CampoHora(horaFim, { horaFim = it }, "Fim")
        CampoTexto(local, { local = it }, "Local")
        CampoTexto(observacoes, { observacoes = it }, "Observações", linhasMultiplas = true)
    }
}
