package com.brunnakampferd.temvorax.ui.components.form

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.brunnakampferd.temvorax.ui.components.TemvoraxTextField
import com.brunnakampferd.temvorax.ui.theme.CremeCard
import com.brunnakampferd.temvorax.ui.theme.RoxoPrimario
import com.brunnakampferd.temvorax.ui.theme.TextoPrincipal
import com.brunnakampferd.temvorax.ui.theme.TextoSecundario
import com.brunnakampferd.temvorax.ui.util.nomeDiaCurto
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Campos de formulário reutilizados por todas as sub-telas de Acadêmica,
 * Profissional e Pessoal — pra cada tela de "adicionar/editar" ficar só
 * juntando esses campos, em vez de reimplementar text field/date picker/etc.
 * toda vez.
 */

@Composable
fun CampoTexto(
    valor: String,
    aoMudar: (String) -> Unit,
    rotulo: String,
    modifier: Modifier = Modifier,
    linhasMultiplas: Boolean = false
) {
    TemvoraxTextField(
        valor = valor,
        aoMudar = aoMudar,
        rotulo = rotulo,
        modifier = modifier.fillMaxWidth(),
        singleLine = !linhasMultiplas
    )
}

@Composable
fun CampoNumero(
    valor: Double,
    aoMudar: (Double) -> Unit,
    rotulo: String,
    modifier: Modifier = Modifier
) {
    var texto by remember(valor) { mutableStateOf(if (valor == 0.0) "" else valor.toString()) }
    OutlinedTextField(
        value = texto,
        onValueChange = { novo ->
            texto = novo
            novo.replace(",", ".").toDoubleOrNull()?.let(aoMudar)
        },
        label = { Text(rotulo) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun CampoBooleano(
    valor: Boolean,
    aoMudar: (Boolean) -> Unit,
    rotulo: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(rotulo, color = TextoPrincipal)
        Switch(
            checked = valor,
            onCheckedChange = aoMudar,
            colors = SwitchDefaults.colors(checkedTrackColor = RoxoPrimario)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> CampoSelecao(
    valor: T?,
    opcoes: List<T>,
    rotuloOpcao: (T) -> String,
    rotulo: String,
    aoMudar: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandido by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expandido,
        onExpandedChange = { expandido = it },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = valor?.let(rotuloOpcao) ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(rotulo) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
            opcoes.forEach { opcao ->
                DropdownMenuItem(
                    text = { Text(rotuloOpcao(opcao)) },
                    onClick = { aoMudar(opcao); expandido = false }
                )
            }
        }
    }
}

/**
 * Seleção de múltiplos dias da semana, em chips (ex.: "toda segunda e quarta") —
 * usado onde antes só cabia UM dia por registro (Disciplina, Expediente) e nos
 * compromissos que podem virar recorrentes (Terapia etc.).
 */
@Composable
fun CampoMultiSelecaoDias(
    valor: Set<DayOfWeek>,
    aoMudar: (Set<DayOfWeek>) -> Unit,
    rotulo: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(rotulo, color = TextoSecundario, fontSize = 12.sp)
        Spacer(Modifier.height(6.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DayOfWeek.entries.forEach { dia ->
                val selecionado = dia in valor
                FilterChip(
                    selected = selecionado,
                    onClick = { aoMudar(if (selecionado) valor - dia else valor + dia) },
                    label = { Text(nomeDiaCurto(dia)) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoData(
    valor: LocalDate?,
    aoMudar: (LocalDate?) -> Unit,
    rotulo: String,
    modifier: Modifier = Modifier
) {
    var mostrarDialogo by remember { mutableStateOf(false) }
    val texto = valor?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: ""

    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = texto,
            onValueChange = {},
            label = { Text(rotulo) },
            readOnly = true,
            trailingIcon = { androidx.compose.material3.Icon(Icons.Filled.CalendarMonth, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { mostrarDialogo = true }
        )
    }

    if (mostrarDialogo) {
        val estado = rememberDatePickerState(
            initialSelectedDateMillis = valor?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { mostrarDialogo = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = estado.selectedDateMillis
                    aoMudar(millis?.let { Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate() })
                    mostrarDialogo = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = estado)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoHora(
    valor: LocalTime?,
    aoMudar: (LocalTime?) -> Unit,
    rotulo: String,
    modifier: Modifier = Modifier
) {
    var mostrarDialogo by remember { mutableStateOf(false) }
    val texto = valor?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: ""

    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = texto,
            onValueChange = {},
            label = { Text(rotulo) },
            readOnly = true,
            trailingIcon = { androidx.compose.material3.Icon(Icons.Filled.Schedule, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { mostrarDialogo = true }
        )
    }

    if (mostrarDialogo) {
        val estado = rememberTimePickerState(
            initialHour = valor?.hour ?: 8,
            initialMinute = valor?.minute ?: 0,
            is24Hour = true
        )
        Dialog(onDismissRequest = { mostrarDialogo = false }) {
            Surface(shape = RoundedCornerShape(20.dp), color = CremeCard) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TimePicker(state = estado)
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        TextButton(onClick = { mostrarDialogo = false }) { Text("Cancelar") }
                        TextButton(onClick = {
                            aoMudar(LocalTime.of(estado.hour, estado.minute))
                            mostrarDialogo = false
                        }) { Text("OK") }
                    }
                }
            }
        }
    }
}
