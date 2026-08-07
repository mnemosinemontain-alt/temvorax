package com.brunnakampferd.temvorax.ui.screens.profissional

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.brunnakampferd.temvorax.data.model.Expediente
import com.brunnakampferd.temvorax.ui.components.CartaoItemGenerico
import com.brunnakampferd.temvorax.ui.components.ListaGenericaScaffold
import com.brunnakampferd.temvorax.ui.components.TelaFormularioScaffold
import com.brunnakampferd.temvorax.ui.components.form.CampoHora
import com.brunnakampferd.temvorax.ui.components.form.CampoMultiSelecaoDias
import com.brunnakampferd.temvorax.ui.components.form.CampoTexto
import com.brunnakampferd.temvorax.ui.theme.AzulProfissional
import com.brunnakampferd.temvorax.ui.util.formatarDiasSemana
import java.time.DayOfWeek
import java.time.LocalTime

/** Horário de trabalho recorrente (ex.: seg-sex, 09h-18h). */
@Composable
fun ExpedienteScreen(
    expedientes: List<Expediente>,
    onVoltar: () -> Unit,
    onAdicionar: () -> Unit,
    onEditar: (Expediente) -> Unit,
    onExcluir: (Expediente) -> Unit,
    modifier: Modifier = Modifier
) {
    ListaGenericaScaffold(
        titulo = "Expediente",
        // Sem ORDER BY no SQL (diasSemana virou conjunto) — ordena aqui pelo
        // primeiro dia marcado, na ordem natural da semana.
        itens = expedientes.sortedBy { it.diasSemana.minOrNull() },
        chave = { it.id },
        onVoltar = onVoltar,
        onAdicionar = onAdicionar,
        mensagemVazia = "Nenhum expediente cadastrado ainda. Toque em + pra adicionar seus horários de trabalho.",
        modifier = modifier
    ) { item ->
        CartaoItemGenerico(
            titulo = if (item.diasSemana.isEmpty()) "Sem dia definido" else formatarDiasSemana(item.diasSemana),
            subtitulo = "${item.horaInicio} – ${item.horaFim}" + (item.local.takeIf { it.isNotBlank() }?.let { " • $it" } ?: ""),
            corLateral = AzulProfissional,
            onClick = { onEditar(item) },
            onExcluir = { onExcluir(item) }
        )
    }
}

@Composable
fun ExpedienteFormScreen(
    expediente: Expediente?,
    onVoltar: () -> Unit,
    onSalvar: (Expediente) -> Unit,
    modifier: Modifier = Modifier,
    onExcluir: (() -> Unit)? = null
) {
    var diasSemana by remember { mutableStateOf(expediente?.diasSemana ?: setOf(DayOfWeek.MONDAY)) }
    var horaInicio by remember { mutableStateOf(expediente?.horaInicio ?: LocalTime.of(9, 0)) }
    var horaFim by remember { mutableStateOf(expediente?.horaFim ?: LocalTime.of(18, 0)) }
    var local by remember { mutableStateOf(expediente?.local ?: "") }
    var observacoes by remember { mutableStateOf(expediente?.observacoes ?: "") }

    TelaFormularioScaffold(
        titulo = if (expediente == null) "Novo expediente" else "Editar expediente",
        onVoltar = onVoltar,
        onExcluir = onExcluir,
        onSalvar = {
            onSalvar(
                (expediente ?: Expediente(diasSemana = diasSemana, horaInicio = horaInicio, horaFim = horaFim)).copy(
                    diasSemana = diasSemana, horaInicio = horaInicio, horaFim = horaFim, local = local, observacoes = observacoes
                )
            )
        },
        modifier = modifier
    ) {
        CampoMultiSelecaoDias(diasSemana, { diasSemana = it }, "Dias da semana")
        CampoHora(horaInicio, { it?.let { h -> horaInicio = h } }, "Início")
        CampoHora(horaFim, { it?.let { h -> horaFim = h } }, "Fim")
        CampoTexto(local, { local = it }, "Local")
        CampoTexto(observacoes, { observacoes = it }, "Observações", linhasMultiplas = true)
    }
}
