package com.brunnakampferd.temvorax.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.brunnakampferd.temvorax.ui.theme.CremeCard
import com.brunnakampferd.temvorax.ui.theme.LavandaClaro
import com.brunnakampferd.temvorax.ui.theme.RoxoPrimario
import com.brunnakampferd.temvorax.ui.theme.TextoSecundario

/**
 * As três abas principais do app: o que tem pra hoje, o que ainda está
 * pendente (atrasado ou não) e o que já foi concluído.
 *
 * Perfil, Configurações e Loja de recompensas ainda não têm lugar aqui —
 * fica pra quando a gente desenhar isso com calma.
 */
enum class AbaPrincipal(val rotulo: String, val icone: ImageVector) {
    HOJE("Hoje", Icons.Filled.Today),
    PENDENCIAS("Pendências", Icons.Filled.PendingActions),
    CONCLUIDO("Concluído", Icons.Filled.CheckCircle)
}

@Composable
fun NavegacaoPrincipal(
    abaSelecionada: AbaPrincipal,
    aoSelecionar: (AbaPrincipal) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = CremeCard
    ) {
        AbaPrincipal.entries.forEach { aba ->
            NavigationBarItem(
                selected = abaSelecionada == aba,
                onClick = { aoSelecionar(aba) },
                icon = { Icon(imageVector = aba.icone, contentDescription = aba.rotulo) },
                label = { Text(aba.rotulo) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = RoxoPrimario,
                    selectedTextColor = RoxoPrimario,
                    indicatorColor = LavandaClaro,
                    unselectedIconColor = TextoSecundario,
                    unselectedTextColor = TextoSecundario
                )
            )
        }
    }
}
