package com.brunnakampferd.temvorax.ui.screens.academico

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.brunnakampferd.temvorax.data.model.Disciplina
import com.brunnakampferd.temvorax.data.model.Prova
import com.brunnakampferd.temvorax.data.model.Semestre
import com.brunnakampferd.temvorax.data.model.TrabalhoAcademico
import com.brunnakampferd.temvorax.ui.components.CabecalhoComVoltar
import com.brunnakampferd.temvorax.ui.components.CartaoItemGenerico
import com.brunnakampferd.temvorax.ui.components.CheckboxComReforco
import com.brunnakampferd.temvorax.ui.theme.CremeClaro
import com.brunnakampferd.temvorax.ui.theme.RoxoPrimario
import com.brunnakampferd.temvorax.ui.theme.TextoSecundario
import com.brunnakampferd.temvorax.ui.theme.TextoSobreRoxo
import com.brunnakampferd.temvorax.ui.theme.VerdeAcademico
import com.brunnakampferd.temvorax.ui.util.formatarCurta
import com.brunnakampferd.temvorax.ui.util.formatarDiasSemana

/**
 * Detalhe de um semestre: Disciplinas, Provas, Trabalhos e Frequência daquele
 * período específico, em abas — é aqui que "semestre funciona como pasta".
 */
@Composable
fun SemestreDetalheScreen(
    semestre: Semestre?,
    disciplinas: List<Disciplina>,
    provas: List<Prova>,
    trabalhos: List<TrabalhoAcademico>,
    onVoltar: () -> Unit,
    onAdicionarDisciplina: () -> Unit,
    onEditarDisciplina: (Disciplina) -> Unit,
    onExcluirDisciplina: (Disciplina) -> Unit,
    onAdicionarProva: () -> Unit,
    onEditarProva: (Prova) -> Unit,
    onExcluirProva: (Prova) -> Unit,
    onAdicionarTrabalho: () -> Unit,
    onEditarTrabalho: (TrabalhoAcademico) -> Unit,
    onAlternarTrabalho: (TrabalhoAcademico) -> Unit,
    onExcluirTrabalho: (TrabalhoAcademico) -> Unit,
    onAbrirFrequencia: (Disciplina) -> Unit,
    modifier: Modifier = Modifier
) {
    var abaSelecionada by remember { mutableIntStateOf(0) }
    val abas = listOf("Disciplinas", "Provas", "Trabalhos", "Frequência")

    Scaffold(
        modifier = modifier,
        containerColor = CremeClaro,
        topBar = { CabecalhoComVoltar(titulo = semestre?.nome ?: "Semestre", onVoltar = onVoltar) },
        floatingActionButton = {
            if (abaSelecionada != 3) {
                FloatingActionButton(
                    onClick = when (abaSelecionada) {
                        0 -> onAdicionarDisciplina
                        1 -> onAdicionarProva
                        else -> onAdicionarTrabalho
                    },
                    containerColor = RoxoPrimario,
                    contentColor = TextoSobreRoxo
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Adicionar")
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // ScrollableTabRow em vez de TabRow: cada aba fica do tamanho do
            // próprio texto (uma linha só) em vez de dividir a tela em 4
            // partes iguais — era isso que fazia "Disciplinas" e "Frequência"
            // quebrarem em 2 linhas.
            ScrollableTabRow(selectedTabIndex = abaSelecionada, containerColor = CremeClaro, contentColor = RoxoPrimario) {
                abas.forEachIndexed { indice, titulo ->
                    Tab(selected = abaSelecionada == indice, onClick = { abaSelecionada = indice }, text = { Text(titulo) })
                }
            }

            when (abaSelecionada) {
                0 -> ListaAba(disciplinas, "Nenhuma disciplina ainda. Toque em + pra adicionar.") { d ->
                    CartaoItemGenerico(
                        titulo = d.nome,
                        subtitulo = listOfNotNull(
                            d.professor.takeIf { it.isNotBlank() },
                            d.diasSemana.takeIf { it.isNotEmpty() }?.let { dias ->
                                val horario = if (d.horaInicio != null && d.horaFim != null) " ${d.horaInicio}–${d.horaFim}" else ""
                                "${formatarDiasSemana(dias)}$horario"
                            }
                        ).joinToString(" • ").ifBlank { null },
                        corLateral = VerdeAcademico,
                        onClick = { onEditarDisciplina(d) },
                        onExcluir = { onExcluirDisciplina(d) },
                        trailing = { TextButton(onClick = { onAbrirFrequencia(d) }) { Text("Frequência") } }
                    )
                }
                1 -> ListaAba(provas, "Nenhuma prova ainda. Toque em + pra adicionar.") { p ->
                    CartaoItemGenerico(
                        titulo = p.titulo,
                        subtitulo = p.data.formatarCurta(),
                        corLateral = VerdeAcademico,
                        onClick = { onEditarProva(p) },
                        onExcluir = { onExcluirProva(p) }
                    )
                }
                2 -> ListaAba(trabalhos, "Nenhum trabalho ainda. Toque em + pra adicionar.") { t ->
                    CartaoItemGenerico(
                        titulo = t.titulo,
                        subtitulo = "${if (t.concluido) "Entregue" else "Entrega"} ${t.dataEntrega.formatarCurta()}",
                        corLateral = VerdeAcademico,
                        onClick = { onEditarTrabalho(t) },
                        onExcluir = { onExcluirTrabalho(t) },
                        trailing = {
                            CheckboxComReforco(checked = t.concluido, onCheckedChange = { onAlternarTrabalho(t) })
                        }
                    )
                }
                else -> ListaAba(disciplinas, "Cadastre disciplinas nessa aba \"Disciplinas\" pra acompanhar frequência.") { d ->
                    CartaoItemGenerico(
                        titulo = d.nome,
                        corLateral = VerdeAcademico,
                        onClick = { onAbrirFrequencia(d) }
                    )
                }
            }
        }
    }
}

@Composable
private fun <T> ListaAba(itens: List<T>, mensagemVazia: String, item: @Composable (T) -> Unit) {
    if (itens.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize().padding(40.dp), contentAlignment = Alignment.Center) {
            Text(text = mensagemVazia, color = TextoSecundario, textAlign = TextAlign.Center)
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(itens) { item(it) }
        }
    }
}
