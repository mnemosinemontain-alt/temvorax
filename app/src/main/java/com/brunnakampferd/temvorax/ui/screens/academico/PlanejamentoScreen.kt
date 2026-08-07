package com.brunnakampferd.temvorax.ui.screens.academico

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brunnakampferd.temvorax.domain.AlertaPlanejamento
import com.brunnakampferd.temvorax.domain.PlanejamentoResultado
import com.brunnakampferd.temvorax.domain.formatarHoras
import com.brunnakampferd.temvorax.ui.components.CabecalhoComVoltar
import com.brunnakampferd.temvorax.ui.theme.CremeCard
import com.brunnakampferd.temvorax.ui.theme.CremeClaro
import com.brunnakampferd.temvorax.ui.theme.DouradoDetalhe
import com.brunnakampferd.temvorax.ui.theme.ErroSuave
import com.brunnakampferd.temvorax.ui.theme.RoxoPrimario
import com.brunnakampferd.temvorax.ui.theme.TealPrimario
import com.brunnakampferd.temvorax.ui.theme.TextoPrincipal
import com.brunnakampferd.temvorax.ui.theme.TextoSecundario
import com.brunnakampferd.temvorax.ui.util.formatarCurta
import com.brunnakampferd.temvorax.ui.util.nomeDiaCompleto
import java.time.DayOfWeek

/**
 * Painel de horas ocupadas/livres da semana + alertas — a partir de
 * disciplinas, expediente e compromissos já cadastrados (veja
 * [com.brunnakampferd.temvorax.domain.calcularPlanejamento] pra entender a
 * conta e as simplificações assumidas).
 */
@Composable
fun PlanejamentoScreen(
    resultado: PlanejamentoResultado?,
    carregando: Boolean,
    onVoltar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        containerColor = CremeClaro,
        topBar = { CabecalhoComVoltar(titulo = "Planejamento Inteligente", onVoltar = onVoltar) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            if (carregando || resultado == null) {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 60.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = RoxoPrimario)
                }
            } else {
                Text(
                    text = "Semana de ${resultado.inicioSemana.formatarCurta()} a ${resultado.fimSemana.formatarCurta()}",
                    fontSize = 13.sp,
                    color = TextoSecundario
                )

                Spacer(Modifier.height(12.dp))
                CartaoResumoHoras(resultado)

                Spacer(Modifier.height(20.dp))
                Text("Ocupação por dia", fontWeight = FontWeight.Bold, color = TextoPrincipal)
                Spacer(Modifier.height(8.dp))
                DayOfWeek.values().forEach { dia ->
                    LinhaOcupacaoDia(nomeDiaCompleto(dia), resultado.ocupacaoPorDia[dia] ?: 0.0)
                    Spacer(Modifier.height(6.dp))
                }

                Spacer(Modifier.height(20.dp))
                Text("Alertas", fontWeight = FontWeight.Bold, color = TextoPrincipal)
                Spacer(Modifier.height(8.dp))
                if (resultado.alertas.isEmpty()) {
                    Text("Nenhum alerta agora — sua semana está tranquila. 🌿", color = TextoSecundario, fontSize = 13.sp)
                } else {
                    resultado.alertas.forEach { alerta ->
                        CartaoAlerta(alerta)
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun CartaoResumoHoras(resultado: PlanejamentoResultado) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(14.dp), clip = false)
            .background(CremeCard, RoundedCornerShape(14.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(formatarHoras(resultado.horasOcupadasSemana), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = RoxoPrimario)
            Text("Ocupadas", fontSize = 12.sp, color = TextoSecundario)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(formatarHoras(resultado.horasLivresSemana), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TealPrimario)
            Text("Livres", fontSize = 12.sp, color = TextoSecundario)
        }
    }
}

@Composable
private fun LinhaOcupacaoDia(nomeDia: String, horas: Double) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(nomeDia, color = TextoPrincipal, fontSize = 13.sp)
        Text(
            text = formatarHoras(horas),
            color = if (horas >= 16.0) ErroSuave else TextoSecundario,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun CartaoAlerta(alerta: AlertaPlanejamento) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(14.dp), clip = false)
            .background(CremeCard, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.WarningAmber,
            contentDescription = null,
            tint = DouradoDetalhe,
            modifier = Modifier.padding(end = 10.dp)
        )
        Column {
            Text(alerta.titulo, fontWeight = FontWeight.SemiBold, color = TextoPrincipal, fontSize = 14.sp)
            Text(alerta.descricao, color = TextoSecundario, fontSize = 12.sp)
        }
    }
}
