package com.brunnakampferd.temvorax.ui.screens.academico

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.brunnakampferd.temvorax.data.model.Disciplina
import com.brunnakampferd.temvorax.ui.components.TelaFormularioScaffold
import com.brunnakampferd.temvorax.ui.components.form.CampoData
import com.brunnakampferd.temvorax.ui.components.form.CampoHora
import com.brunnakampferd.temvorax.ui.components.form.CampoMultiSelecaoDias
import com.brunnakampferd.temvorax.ui.components.form.CampoTexto
import java.time.DayOfWeek

@Composable
fun DisciplinaFormScreen(
    semestreId: Long,
    disciplina: Disciplina?,
    onVoltar: () -> Unit,
    onSalvar: (Disciplina) -> Unit,
    modifier: Modifier = Modifier,
    onExcluir: (() -> Unit)? = null
) {
    var nome by remember { mutableStateOf(disciplina?.nome ?: "") }
    var professor by remember { mutableStateOf(disciplina?.professor ?: "") }
    var diasSemana by remember { mutableStateOf<Set<DayOfWeek>>(disciplina?.diasSemana ?: emptySet()) }
    var horaInicio by remember { mutableStateOf(disciplina?.horaInicio) }
    var horaFim by remember { mutableStateOf(disciplina?.horaFim) }
    var dataInicioMateria by remember { mutableStateOf(disciplina?.dataInicioMateria) }
    var dataFimMateria by remember { mutableStateOf(disciplina?.dataFimMateria) }

    TelaFormularioScaffold(
        titulo = if (disciplina == null) "Nova disciplina" else "Editar disciplina",
        onVoltar = onVoltar,
        onExcluir = onExcluir,
        onSalvar = {
            onSalvar(
                (disciplina ?: Disciplina(semestreId = semestreId, nome = nome)).copy(
                    nome = nome,
                    professor = professor,
                    diasSemana = diasSemana,
                    horaInicio = horaInicio,
                    horaFim = horaFim,
                    dataInicioMateria = dataInicioMateria,
                    dataFimMateria = dataFimMateria
                )
            )
        },
        modifier = modifier
    ) {
        CampoTexto(nome, { nome = it }, "Nome da disciplina")
        CampoTexto(professor, { professor = it }, "Professor(a)")
        CampoMultiSelecaoDias(
            valor = diasSemana,
            aoMudar = { diasSemana = it },
            rotulo = "Dias da semana da aula"
        )
        CampoHora(horaInicio, { horaInicio = it }, "Início da aula")
        CampoHora(horaFim, { horaFim = it }, "Fim da aula")
        CampoData(dataInicioMateria, { dataInicioMateria = it }, "Início da matéria (deixe em branco pra valer o semestre inteiro)")
        CampoData(dataFimMateria, { dataFimMateria = it }, "Fim da matéria (deixe em branco pra valer o semestre inteiro)")
    }
}
