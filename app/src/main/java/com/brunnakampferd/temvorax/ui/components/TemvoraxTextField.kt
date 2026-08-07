package com.brunnakampferd.temvorax.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.brunnakampferd.temvorax.ui.theme.CremeCard
import com.brunnakampferd.temvorax.ui.theme.RoxoPrimario
import com.brunnakampferd.temvorax.ui.theme.TextoPrincipal
import com.brunnakampferd.temvorax.ui.theme.TextoSecundario

/**
 * Campo de texto padrão do Temvorax (usado em e-mail, senha, e outros campos futuros).
 *
 * Eu criei um único componente reutilizável em vez de espalhar OutlinedTextField
 * customizado em cada tela — assim, se eu quiser mudar a cor da borda ou o raio
 * do cantinho arredondado no futuro, mudo uma vez só, aqui.
 *
 * @param ehSenha se true, mostra o texto oculto (••••) com um ícone de olho pra alternar
 */
@Composable
fun TemvoraxTextField(
    valor: String,
    aoMudar: (String) -> Unit,
    rotulo: String,
    modifier: Modifier = Modifier,
    ehSenha: Boolean = false,
    ehEmail: Boolean = false,
    singleLine: Boolean = true
) {
    // Controla se a senha está visível ou escondida — só é usado quando ehSenha = true.
    var senhaVisivel by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = valor,
        onValueChange = aoMudar,
        label = { Text(rotulo) },
        singleLine = singleLine,
        shape = RoundedCornerShape(14.dp),
        modifier = modifier,
        visualTransformation = if (ehSenha && !senhaVisivel) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = when {
                ehEmail -> KeyboardType.Email
                ehSenha -> KeyboardType.Password
                else -> KeyboardType.Text
            }
        ),
        trailingIcon = {
            // Só mostra o ícone de olho se o campo for de senha.
            if (ehSenha) {
                val icone = if (senhaVisivel) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                IconButton(onClick = { senhaVisivel = !senhaVisivel }) {
                    Icon(imageVector = icone, contentDescription = "Mostrar/ocultar senha")
                }
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = CremeCard,
            unfocusedContainerColor = CremeCard,
            focusedBorderColor = RoxoPrimario,
            unfocusedBorderColor = TextoSecundario,
            focusedTextColor = TextoPrincipal,
            unfocusedTextColor = TextoPrincipal
        )
    )
}