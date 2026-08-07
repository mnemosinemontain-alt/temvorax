package com.brunnakampferd.temvorax.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brunnakampferd.temvorax.ui.theme.CremeCard
import com.brunnakampferd.temvorax.ui.theme.CremeClaro
import com.brunnakampferd.temvorax.ui.theme.ErroSuave
import com.brunnakampferd.temvorax.ui.theme.RoxoPrimario
import com.brunnakampferd.temvorax.ui.theme.TextoPrincipal
import com.brunnakampferd.temvorax.ui.theme.TextoSecundario
import com.brunnakampferd.temvorax.ui.theme.TextoSobreRoxo

/**
 * Casca padrão de "lista de uma sub-área": cabeçalho com voltar + título,
 * botão "+" flutuante, e a lista (ou uma mensagem de vazio). Todas as
 * sub-telas de Acadêmica/Profissional/Pessoal usam essa mesma casca.
 */
@Composable
fun <T> ListaGenericaScaffold(
    titulo: String,
    itens: List<T>,
    chave: (T) -> Any,
    onVoltar: () -> Unit,
    onAdicionar: () -> Unit,
    modifier: Modifier = Modifier,
    mensagemVazia: String = "Nada por aqui ainda. Toque em + pra adicionar.",
    acaoTopo: (@Composable () -> Unit)? = null,
    itemContent: @Composable (T) -> Unit
) {
    Scaffold(
        modifier = modifier,
        containerColor = CremeClaro,
        topBar = { CabecalhoComVoltar(titulo = titulo, onVoltar = onVoltar, acao = acaoTopo) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAdicionar,
                containerColor = RoxoPrimario,
                contentColor = TextoSobreRoxo
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Adicionar")
            }
        }
    ) { padding ->
        if (itens.isEmpty()) {
            Box(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = mensagemVazia, color = TextoSecundario, modifier = Modifier.padding(horizontal = 40.dp))
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(items = itens, key = chave) { item -> itemContent(item) }
            }
        }
    }
}

/** Cabeçalho simples reutilizado por listas e formulários: seta de voltar + título. */
@Composable
fun CabecalhoComVoltar(
    titulo: String,
    onVoltar: () -> Unit,
    modifier: Modifier = Modifier,
    acao: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onVoltar) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = RoxoPrimario)
        }
        Text(
            text = titulo,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextoPrincipal,
            modifier = Modifier.weight(1f)
        )
        acao?.invoke()
    }
}

/**
 * Card padrão de um item de lista genérica: barra colorida à esquerda
 * (indica categoria/status), título, subtítulo opcional, e botão de excluir.
 */
@Composable
fun CartaoItemGenerico(
    titulo: String,
    modifier: Modifier = Modifier,
    subtitulo: String? = null,
    corLateral: Color = RoxoPrimario,
    onClick: () -> Unit = {},
    onExcluir: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(14.dp), clip = false)
            .background(color = CremeCard, shape = RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 6.dp)
                .width(4.dp)
                .height(36.dp)
                .background(corLateral, RoundedCornerShape(4.dp))
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 10.dp, vertical = 10.dp)
        ) {
            Text(titulo, color = TextoPrincipal, fontWeight = FontWeight.SemiBold)
            if (subtitulo != null) {
                Text(subtitulo, color = TextoSecundario, fontSize = 12.sp)
            }
        }
        trailing?.invoke()
        if (onExcluir != null) {
            IconButton(onClick = onExcluir) {
                Icon(Icons.Filled.Delete, contentDescription = "Excluir", tint = ErroSuave)
            }
        }
    }
}
