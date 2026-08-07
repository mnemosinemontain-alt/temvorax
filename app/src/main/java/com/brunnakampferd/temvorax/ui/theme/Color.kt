package com.brunnakampferd.temvorax.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Paleta oficial do Temvorax.
 *
 * Eu separei em blocos pra facilitar a manutenção: cores base (fundo/superfície),
 * cores de destaque (roxo/teal/dourado) e cores de texto. Assim, se eu precisar
 * ajustar o tom de alguma cor no futuro, mudo em um lugar só e reflete no app inteiro.
 *
 * Regra que eu segui pra escolher os tons: tudo precisa combinar com pixel art
 * suave (sem cores muito saturadas/neon, senão foge da estética indie que queremos).
 */

// ---------- Cores base (fundo e superfícies) ----------
val CremeClaro = Color(0xFFFFF8EC)        // fundo principal, tipo papel antigo/pixel suave
val CremeCard = Color(0xFFFFFDF7)         // fundo de cards, um pouco mais claro que o fundo geral
val LavandaClaro = Color(0xFFE9E1F7)      // fundo secundário, usado em blocos/seções

// ---------- Cores de destaque ----------
val RoxoPrimario = Color(0xFF7C5CBF)      // cor principal da marca, usada em botões primários
val RoxoEscuro = Color(0xFF5B3F94)        // usado em textos sobre fundo claro e estados "pressed"
val AzulClaro = Color(0xFF8FCBFF)         // cor secundária, usada em headers/ícones
val TealPrimario = Color(0xFF3FB6A8)      // cor de destaque para ações positivas (streaks, xp, sucesso)
val DouradoDetalhe = Color(0xFFE8B93F)    // detalhes pequenos: medalhas, conquistas, estrelinhas

// ---------- Cores de texto ----------
val TextoPrincipal = Color(0xFF3A2E52)    // roxo bem escuro, mais suave que preto puro
val TextoSecundario = Color(0xFF7A6F94)   // cinza arroxeado, pra legendas e textos de apoio
val TextoSobreRoxo = Color(0xFFFFFFFF)    // texto branco usado em cima de botões roxos/teal

// ---------- Cores utilitárias ----------
val ErroSuave = Color(0xFFE07A7A)         // erro em tom pastel, mantendo a identidade acolhedora
val SombraSuave = Color(0x1A3A2E52)       // sombra leve translúcida pra dar profundidade sem pesar

// ---------- Cores de área/categoria ----------
// Usadas pra marcar de qual área (Acadêmica/Profissional/Pessoal) uma tarefa é —
// no menu lateral, nas tags dos cards de tarefa, etc. Os tons seguem a mesma
// paleta usada na arte de boas-vindas do Koda (ACADÊMICA verde, PROFISSIONAL
// azul, PESSOAL vermelho/rosa), mas em constantes próprias — assim, se um dia
// a cor de erro (ErroSuave) mudar, a cor de "Pessoal" não muda junto à toa.
val VerdeAcademico = Color(0xFF6BAE75)    // área Acadêmica
val AzulProfissional = Color(0xFF4F8FCC) // área Profissional
val RosaPessoal = Color(0xFFE0728C)       // área Pessoal