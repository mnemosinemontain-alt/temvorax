package com.brunnakampferd.temvorax.ui.screens.hub

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.CalendarViewMonth
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brunnakampferd.temvorax.data.model.AreaTarefa
import com.brunnakampferd.temvorax.ui.components.CabecalhoComVoltar
import com.brunnakampferd.temvorax.ui.components.corDaArea
import com.brunnakampferd.temvorax.ui.navigation.Rotas
import com.brunnakampferd.temvorax.ui.theme.CremeCard
import com.brunnakampferd.temvorax.ui.theme.CremeClaro
import com.brunnakampferd.temvorax.ui.theme.TextoPrincipal

/** Uma sub-tela dentro do hub de uma área (ex.: "Semestres" dentro de Acadêmica). */
data class ItemHub(val rotulo: String, val icone: ImageVector, val rota: String)

fun itensHubAcademica(): List<ItemHub> = listOf(
    ItemHub("Semestres", Icons.Filled.CalendarViewMonth, Rotas.SEMESTRES),
    ItemHub("Planejamento Inteligente", Icons.Filled.Insights, Rotas.PLANEJAMENTO),
    ItemHub("Calendário acadêmico", Icons.Filled.Event, Rotas.CALENDARIO),
    ItemHub("Bolsas e editais", Icons.Filled.CardGiftcard, Rotas.BOLSAS),
    ItemHub("Horas complementares", Icons.Filled.Timelapse, Rotas.HORAS),
    ItemHub("Estágio acadêmico", Icons.Filled.Work, Rotas.ESTAGIO),
    ItemHub("TCC", Icons.Filled.Description, Rotas.TCC)
)

fun itensHubProfissional(): List<ItemHub> = listOf(
    ItemHub("Expediente", Icons.Filled.Schedule, Rotas.EXPEDIENTE),
    ItemHub("Compromisso profissional", Icons.Filled.Event, Rotas.COMPROMISSO_PROFISSIONAL),
    ItemHub("Atividade profissional", Icons.Filled.Checklist, Rotas.ATIVIDADE_PROFISSIONAL)
)

fun itensHubPessoal(): List<ItemHub> = listOf(
    ItemHub("Tarefas diárias", Icons.Filled.Checklist, Rotas.TAREFAS_DIARIAS),
    ItemHub("Compromissos pessoais", Icons.Filled.Event, Rotas.COMPROMISSO_PESSOAL)
)

fun iconeArea(area: AreaTarefa): ImageVector = when (area) {
    AreaTarefa.ACADEMICA -> Icons.Filled.School
    AreaTarefa.PROFISSIONAL -> Icons.Filled.Work
    AreaTarefa.PESSOAL -> Icons.Filled.Favorite
}

/** Tela-hub de uma área: título + grade com as sub-telas dela. */
@Composable
fun AreaHubScreen(
    area: AreaTarefa,
    itens: List<ItemHub>,
    onVoltar: () -> Unit,
    onItemClick: (ItemHub) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        containerColor = CremeClaro,
        topBar = { CabecalhoComVoltar(titulo = area.rotulo, onVoltar = onVoltar) }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(itens) { item ->
                CartaoHub(item = item, cor = corDaArea(area), onClick = { onItemClick(item) })
            }
        }
    }
}

@Composable
private fun CartaoHub(item: ItemHub, cor: androidx.compose.ui.graphics.Color, onClick: () -> Unit) {
    androidx.compose.foundation.layout.Column(
        modifier = Modifier
            .aspectRatio(1f)
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp), clip = false)
            .background(CremeCard, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(imageVector = item.icone, contentDescription = null, tint = cor, modifier = Modifier.padding(bottom = 8.dp))
        Text(
            text = item.rotulo,
            textAlign = TextAlign.Center,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = TextoPrincipal
        )
    }
}
