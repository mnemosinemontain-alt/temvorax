package com.brunnakampferd.temvorax.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.brunnakampferd.temvorax.ui.theme.CremeClaro
import com.brunnakampferd.temvorax.ui.theme.ErroSuave

/**
 * Casca padrão de "adicionar/editar item": cabeçalho com voltar + título
 * (e excluir, se estiver editando algo que já existe), corpo rolável com os
 * campos, e um botão "Salvar" no final. Usada por todas as telas de
 * formulário das sub-áreas.
 */
@Composable
fun TelaFormularioScaffold(
    titulo: String,
    onVoltar: () -> Unit,
    onSalvar: () -> Unit,
    modifier: Modifier = Modifier,
    onExcluir: (() -> Unit)? = null,
    textoBotaoSalvar: String = "Salvar",
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        modifier = modifier,
        containerColor = CremeClaro,
        topBar = {
            CabecalhoComVoltar(
                titulo = titulo,
                onVoltar = onVoltar,
                acao = if (onExcluir != null) {
                    {
                        IconButton(onClick = onExcluir) {
                            Icon(Icons.Filled.Delete, contentDescription = "Excluir", tint = ErroSuave)
                        }
                    }
                } else null
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            content()
            Spacer(Modifier.height(8.dp))
            BotaoPrimario(texto = textoBotaoSalvar, onClick = onSalvar)
            Spacer(Modifier.height(24.dp))
        }
    }
}
