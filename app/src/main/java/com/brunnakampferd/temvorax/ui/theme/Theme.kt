package com.brunnakampferd.temvorax.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/**
 * Esquema de cores do Temvorax, montado em cima da nossa paleta (Color.kt).
 *
 * DECISÃO DE DESIGN: o Temvorax SEMPRE usa esse tema claro, mesmo que o
 * celular do usuário esteja no modo escuro do sistema. Isso é de propósito:
 * a identidade visual (cores quentes, pixel art) é parte da experiência do
 * app, então não queremos que o modo escuro do Android "brigue" com isso.
 * Muitos apps com forte identidade de marca fazem essa mesma escolha.
 *
 * Se um dia vocês decidirem oferecer um modo escuro de verdade, dá pra criar
 * uma segunda paleta (tons escuros que combinem com a estética) e alternar
 * aqui — mas por enquanto, mantemos simples.
 */
private val TemvoraxColorScheme = lightColorScheme(
    primary = RoxoPrimario,
    onPrimary = TextoSobreRoxo,
    primaryContainer = LavandaClaro,
    onPrimaryContainer = TextoPrincipal,

    secondary = TealPrimario,
    onSecondary = TextoSobreRoxo,

    tertiary = DouradoDetalhe,
    onTertiary = TextoPrincipal,

    background = CremeClaro,
    onBackground = TextoPrincipal,

    surface = CremeCard,
    onSurface = TextoPrincipal,
    surfaceVariant = LavandaClaro,
    onSurfaceVariant = TextoSecundario,

    error = ErroSuave,
    onError = TextoSobreRoxo,

    outline = TextoSecundario
)

/**
 * Tema principal do app. Envolve toda a UI (chamado uma vez lá no MainActivity,
 * por cima de tudo) pra que qualquer Composable filho tenha acesso automático
 * às cores e tipografia através do MaterialTheme.
 */
@Composable
fun TemvoraxTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = TemvoraxColorScheme,
        typography = Typography(), // por enquanto usa a tipografia padrão do Material3
        content = content
    )
}