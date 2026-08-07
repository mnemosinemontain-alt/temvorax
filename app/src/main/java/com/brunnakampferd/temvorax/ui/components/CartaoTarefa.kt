package com.brunnakampferd.temvorax.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brunnakampferd.temvorax.data.model.TarefaDia
import com.brunnakampferd.temvorax.ui.theme.CremeCard
import com.brunnakampferd.temvorax.ui.theme.ErroSuave
import com.brunnakampferd.temvorax.ui.theme.TextoPrincipal
import com.brunnakampferd.temvorax.ui.theme.TextoSecundario
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val formatoDataCurta = DateTimeFormatter.ofPattern("dd/MM")

/**
 * Cartão de uma tarefa/compromisso, com checkbox pra marcar como concluída
 * e um selo colorido indicando a área (Acadêmica/Profissional/Pessoal) dela —
 * cor e ícone vêm de [corDaArea]/[iconeDaArea], então ficam sempre iguais em
 * qualquer lugar do app que mostrar essa mesma área.
 *
 * O card inteiro é clicável (não só o checkbox), pra ficar mais fácil de
 * marcar em telas pequenas.
 *
 * @param mostrarData se true, exibe a data da tarefa embaixo do título (útil
 *   nas abas Pendências/Concluído, onde as tarefas não são todas de hoje).
 *   Se a tarefa estiver atrasada (data no passado e ainda não concluída), a
 *   data aparece destacada em vermelho.
 * @param hoje a data de "hoje", usada só pra decidir se a tarefa está atrasada.
 * @param aoExcluir chamado ao tocar no ícone de excluir — toda tarefa criada pelo "+"
 *   da Home precisa poder ser removida daqui, não só marcada como concluída.
 */
@Composable
fun CartaoTarefa(
    tarefa: TarefaDia,
    aoAlternar: () -> Unit,
    aoExcluir: () -> Unit,
    modifier: Modifier = Modifier,
    mostrarData: Boolean = false,
    hoje: LocalDate = LocalDate.now()
) {
    val atrasada = mostrarData && !tarefa.concluida && tarefa.data.isBefore(hoje)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(14.dp), clip = false)
            .background(color = CremeCard, shape = RoundedCornerShape(14.dp))
            .clickable(onClick = aoAlternar)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CheckboxComReforco(
            checked = tarefa.concluida,
            onCheckedChange = { aoAlternar() }
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = tarefa.titulo,
                color = if (tarefa.concluida) TextoSecundario else TextoPrincipal,
                textDecoration = if (tarefa.concluida) TextDecoration.LineThrough else TextDecoration.None
            )
            if (mostrarData) {
                Text(
                    text = if (atrasada) {
                        "Atrasada • ${tarefa.data.format(formatoDataCurta)}"
                    } else {
                        tarefa.data.format(formatoDataCurta)
                    },
                    fontSize = 12.sp,
                    color = if (atrasada) ErroSuave else TextoSecundario
                )
            }
        }

        SeloArea(area = tarefa.area)

        IconButton(onClick = aoExcluir) {
            Icon(Icons.Filled.Delete, contentDescription = "Excluir", tint = ErroSuave)
        }
    }
}

/** Selinho circular colorido com o ícone da área — a "tag" visual da tarefa. */
@Composable
private fun SeloArea(area: com.brunnakampferd.temvorax.data.model.AreaTarefa) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .background(color = corDaArea(area), shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = iconeDaArea(area),
            contentDescription = area.rotulo,
            tint = androidx.compose.ui.graphics.Color.White,
            modifier = Modifier.size(14.dp)
        )
    }
}
