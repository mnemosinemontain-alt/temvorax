package com.brunnakampferd.temvorax.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brunnakampferd.temvorax.data.model.AreaTarefa
import com.brunnakampferd.temvorax.ui.theme.CremeClaro
import com.brunnakampferd.temvorax.ui.theme.RoxoPrimario
import com.brunnakampferd.temvorax.ui.theme.TextoPrincipal

/**
 * Conteúdo do menu lateral de áreas (drawer da esquerda): "Todas" fica na
 * Home (Hoje/Pendências/Concluído, sem filtro); escolher Acadêmica,
 * Profissional ou Pessoal navega pra um hub dedicado daquela área, com as
 * sub-telas dela (Semestres, Provas, Expediente, etc).
 *
 * "Todas" é representado como `areaSelecionada = null` — não é uma área de
 * verdade, é só o estado "sem filtro"/"na Home".
 */
@Composable
fun MenuLateralAreas(
    areaSelecionada: AreaTarefa?,
    aoSelecionarArea: (AreaTarefa?) -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier,
        drawerContainerColor = CremeClaro
    ) {
        Text(
            text = "Escolha sua área",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextoPrincipal,
            modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 12.dp)
        )

        ItemMenuArea(
            rotulo = "Todas",
            icone = Icons.Filled.Apps,
            cor = RoxoPrimario,
            selecionado = areaSelecionada == null,
            onClick = { aoSelecionarArea(null) }
        )

        AreaTarefa.entries.forEach { area ->
            ItemMenuArea(
                rotulo = area.rotulo,
                icone = iconeDaArea(area),
                cor = corDaArea(area),
                selecionado = areaSelecionada == area,
                onClick = { aoSelecionarArea(area) }
            )
        }
    }
}

@Composable
private fun ItemMenuArea(
    rotulo: String,
    icone: ImageVector,
    cor: Color,
    selecionado: Boolean,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = { Text(rotulo) },
        selected = selecionado,
        onClick = onClick,
        icon = { Icon(imageVector = icone, contentDescription = null, tint = cor) },
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = cor.copy(alpha = 0.15f),
            unselectedContainerColor = Color.Transparent,
            selectedTextColor = TextoPrincipal,
            unselectedTextColor = TextoPrincipal
        ),
        modifier = Modifier.padding(horizontal = 12.dp)
    )
}
