package com.brunnakampferd.temvorax.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.brunnakampferd.temvorax.data.model.AreaTarefa
import com.brunnakampferd.temvorax.ui.theme.AzulProfissional
import com.brunnakampferd.temvorax.ui.theme.RosaPessoal
import com.brunnakampferd.temvorax.ui.theme.VerdeAcademico

/**
 * De qual cor e ícone cada área é representada na UI.
 *
 * Centralizado aqui (em vez de espalhar `when` de cor/ícone em cada tela)
 * pra garantir que o menu lateral, as tags dos cards de tarefa e qualquer
 * outro lugar futuro usem sempre a mesma combinação cor+ícone por área.
 */
fun corDaArea(area: AreaTarefa): Color = when (area) {
    AreaTarefa.ACADEMICA -> VerdeAcademico
    AreaTarefa.PROFISSIONAL -> AzulProfissional
    AreaTarefa.PESSOAL -> RosaPessoal
}

fun iconeDaArea(area: AreaTarefa): ImageVector = when (area) {
    AreaTarefa.ACADEMICA -> Icons.Filled.School
    AreaTarefa.PROFISSIONAL -> Icons.Filled.Work
    AreaTarefa.PESSOAL -> Icons.Filled.Favorite
}
