package com.brunnakampferd.temvorax.ui.screens.academico

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.brunnakampferd.temvorax.data.model.BolsaEdital
import com.brunnakampferd.temvorax.data.model.StatusBolsa
import com.brunnakampferd.temvorax.data.model.TipoBolsaEdital
import com.brunnakampferd.temvorax.ui.components.CabecalhoComVoltar
import com.brunnakampferd.temvorax.ui.components.CartaoItemGenerico
import com.brunnakampferd.temvorax.ui.components.TelaFormularioScaffold
import com.brunnakampferd.temvorax.ui.components.form.CampoData
import com.brunnakampferd.temvorax.ui.components.form.CampoSelecao
import com.brunnakampferd.temvorax.ui.components.form.CampoTexto
import com.brunnakampferd.temvorax.ui.theme.CremeClaro
import com.brunnakampferd.temvorax.ui.theme.DouradoDetalhe
import com.brunnakampferd.temvorax.ui.theme.ErroSuave
import com.brunnakampferd.temvorax.ui.theme.RoxoPrimario
import com.brunnakampferd.temvorax.ui.theme.TealPrimario
import com.brunnakampferd.temvorax.ui.theme.TextoSecundario
import com.brunnakampferd.temvorax.ui.theme.TextoSobreRoxo
import com.brunnakampferd.temvorax.ui.util.formatarCurta

/**
 * Bolsas e Editais em abas separadas — mesma lista de dados, filtrada por
 * [TipoBolsaEdital]. O "+" cria um item já do tipo da aba aberta no momento
 * (ver [onAdicionar]).
 */
@Composable
fun BolsasEditaisScreen(
    bolsas: List<BolsaEdital>,
    onVoltar: () -> Unit,
    onAdicionar: (TipoBolsaEdital) -> Unit,
    onEditar: (BolsaEdital) -> Unit,
    onExcluir: (BolsaEdital) -> Unit,
    modifier: Modifier = Modifier
) {
    var abaSelecionada by remember { mutableStateOf(TipoBolsaEdital.BOLSA) }
    val itensDaAba = bolsas.filter { it.tipo == abaSelecionada }

    Scaffold(
        modifier = modifier,
        containerColor = CremeClaro,
        topBar = { CabecalhoComVoltar(titulo = "Bolsas e editais", onVoltar = onVoltar) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAdicionar(abaSelecionada) },
                containerColor = RoxoPrimario,
                contentColor = TextoSobreRoxo
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Adicionar")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            TabRow(selectedTabIndex = abaSelecionada.ordinal, containerColor = CremeClaro, contentColor = RoxoPrimario) {
                TipoBolsaEdital.entries.forEach { tipo ->
                    Tab(
                        selected = abaSelecionada == tipo,
                        onClick = { abaSelecionada = tipo },
                        text = { Text(tipo.rotuloPlural) }
                    )
                }
            }

            if (itensDaAba.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = mensagemVazia(abaSelecionada),
                        color = TextoSecundario,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 40.dp)
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(items = itensDaAba, key = { it.id }) { bolsa ->
                        CartaoItemGenerico(
                            titulo = bolsa.nome,
                            subtitulo = listOfNotNull(
                                bolsa.status.rotulo,
                                bolsa.prazoFinal?.let { "Prazo: ${it.formatarCurta()}" }
                            ).joinToString(" • "),
                            corLateral = corStatusBolsa(bolsa.status),
                            onClick = { onEditar(bolsa) },
                            onExcluir = { onExcluir(bolsa) }
                        )
                    }
                }
            }
        }
    }
}

private fun mensagemVazia(tipo: TipoBolsaEdital) = when (tipo) {
    TipoBolsaEdital.BOLSA -> "Nenhuma bolsa cadastrada ainda."
    TipoBolsaEdital.EDITAL -> "Nenhum edital cadastrado ainda."
}

private fun tituloNovo(tipo: TipoBolsaEdital) = when (tipo) {
    TipoBolsaEdital.BOLSA -> "Nova bolsa"
    TipoBolsaEdital.EDITAL -> "Novo edital"
}

private fun corStatusBolsa(status: StatusBolsa) = when (status) {
    StatusBolsa.APROVADO -> TealPrimario
    StatusBolsa.REPROVADO -> ErroSuave
    StatusBolsa.INSCRITO -> DouradoDetalhe
    StatusBolsa.NAO_INSCRITO -> TextoSecundario
}

@Composable
fun BolsaEditalFormScreen(
    bolsa: BolsaEdital?,
    tipoPadrao: TipoBolsaEdital,
    onVoltar: () -> Unit,
    onSalvar: (BolsaEdital) -> Unit,
    modifier: Modifier = Modifier,
    onExcluir: (() -> Unit)? = null
) {
    var nome by remember { mutableStateOf(bolsa?.nome ?: "") }
    var tipo by remember { mutableStateOf(bolsa?.tipo ?: tipoPadrao) }
    var instituicao by remember { mutableStateOf(bolsa?.instituicao ?: "") }
    var dataAbertura by remember { mutableStateOf(bolsa?.dataAberturaInscricoes) }
    var prazoFinal by remember { mutableStateOf(bolsa?.prazoFinal) }
    var valor by remember { mutableStateOf(bolsa?.valor ?: "") }
    var status by remember { mutableStateOf(bolsa?.status ?: StatusBolsa.NAO_INSCRITO) }
    var observacoes by remember { mutableStateOf(bolsa?.observacoes ?: "") }

    TelaFormularioScaffold(
        titulo = if (bolsa == null) tituloNovo(tipo) else "Editar ${tipo.rotulo.lowercase()}",
        onVoltar = onVoltar,
        onExcluir = onExcluir,
        onSalvar = {
            onSalvar(
                (bolsa ?: BolsaEdital(nome = nome)).copy(
                    nome = nome,
                    tipo = tipo,
                    instituicao = instituicao,
                    dataAberturaInscricoes = dataAbertura,
                    prazoFinal = prazoFinal,
                    valor = valor,
                    status = status,
                    observacoes = observacoes
                )
            )
        },
        modifier = modifier
    ) {
        CampoSelecao(tipo, TipoBolsaEdital.entries, { it.rotulo }, "Tipo", { tipo = it })
        CampoTexto(nome, { nome = it }, "Nome da bolsa/edital")
        CampoTexto(instituicao, { instituicao = it }, "Instituição/órgão responsável")
        CampoData(dataAbertura, { dataAbertura = it }, "Abertura das inscrições")
        CampoData(prazoFinal, { prazoFinal = it }, "Prazo final")
        CampoTexto(valor, { valor = it }, "Valor (se aplicável)")
        CampoSelecao(status, StatusBolsa.entries, { it.rotulo }, "Status", { status = it })
        CampoTexto(observacoes, { observacoes = it }, "Observações", linhasMultiplas = true)
    }
}
