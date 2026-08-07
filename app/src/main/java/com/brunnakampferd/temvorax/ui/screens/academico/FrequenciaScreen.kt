package com.brunnakampferd.temvorax.ui.screens.academico

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.brunnakampferd.temvorax.data.model.Disciplina
import com.brunnakampferd.temvorax.data.model.RegistroFrequencia
import com.brunnakampferd.temvorax.ui.components.CabecalhoComVoltar
import com.brunnakampferd.temvorax.ui.components.CartaoItemGenerico
import com.brunnakampferd.temvorax.ui.components.form.CampoBooleano
import com.brunnakampferd.temvorax.ui.components.form.CampoData
import com.brunnakampferd.temvorax.ui.theme.CremeClaro
import com.brunnakampferd.temvorax.ui.theme.ErroSuave
import com.brunnakampferd.temvorax.ui.theme.RoxoPrimario
import com.brunnakampferd.temvorax.ui.theme.TealPrimario
import com.brunnakampferd.temvorax.ui.theme.TextoSecundario
import com.brunnakampferd.temvorax.ui.theme.TextoSobreRoxo
import com.brunnakampferd.temvorax.ui.util.formatarCurta
import java.time.LocalDate

/** Registro de presença/falta por aula de uma disciplina, com resumo de % de presença. */
@Composable
fun FrequenciaScreen(
    disciplina: Disciplina?,
    registros: List<RegistroFrequencia>,
    onVoltar: () -> Unit,
    onAdicionar: (LocalDate, Boolean) -> Unit,
    onExcluir: (RegistroFrequencia) -> Unit,
    modifier: Modifier = Modifier
) {
    var mostrarDialogo by remember { mutableStateOf(false) }
    val totalAulas = registros.size
    val presencas = registros.count { it.presente }
    val percentual = if (totalAulas > 0) (presencas * 100 / totalAulas) else 100

    Scaffold(
        modifier = modifier,
        containerColor = CremeClaro,
        topBar = { CabecalhoComVoltar(titulo = "Frequência — ${disciplina?.nome ?: ""}", onVoltar = onVoltar) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mostrarDialogo = true },
                containerColor = RoxoPrimario,
                contentColor = TextoSobreRoxo
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Registrar aula")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(horizontal = 20.dp)) {
            if (totalAulas > 0) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Presença: $percentual% ($presencas de $totalAulas aulas)",
                    color = if (percentual < 75) ErroSuave else TealPrimario,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(12.dp))
            }
            if (registros.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Nenhum registro ainda. Toque em + a cada aula.",
                        color = TextoSecundario,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(items = registros, key = { it.id }) { registro ->
                        CartaoItemGenerico(
                            titulo = registro.data.formatarCurta(),
                            subtitulo = if (registro.presente) "Presente" else "Faltou",
                            corLateral = if (registro.presente) TealPrimario else ErroSuave,
                            onExcluir = { onExcluir(registro) }
                        )
                    }
                }
            }
        }
    }

    if (mostrarDialogo) {
        var data by remember { mutableStateOf(LocalDate.now()) }
        var presente by remember { mutableStateOf(true) }
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Registrar aula") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    CampoData(data, { it?.let { d -> data = d } }, "Data da aula")
                    CampoBooleano(presente, { presente = it }, "Presente")
                }
            },
            confirmButton = {
                TextButton(onClick = { onAdicionar(data, presente); mostrarDialogo = false }) { Text("Registrar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) { Text("Cancelar") }
            }
        )
    }
}
