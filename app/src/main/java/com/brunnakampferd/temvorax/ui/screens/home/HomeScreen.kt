package com.brunnakampferd.temvorax.ui.screens.home

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.brunnakampferd.temvorax.data.model.AreaTarefa
import com.brunnakampferd.temvorax.data.model.AtividadeProfissional
import com.brunnakampferd.temvorax.data.model.CompromissoPessoal
import com.brunnakampferd.temvorax.data.model.CompromissoProfissional
import com.brunnakampferd.temvorax.data.model.Disciplina
import com.brunnakampferd.temvorax.data.model.Expediente
import com.brunnakampferd.temvorax.data.model.Perfil
import com.brunnakampferd.temvorax.data.model.Prova
import com.brunnakampferd.temvorax.data.model.TarefaDia
import com.brunnakampferd.temvorax.data.model.TarefaDiaria
import com.brunnakampferd.temvorax.data.model.TrabalhoAcademico
import com.brunnakampferd.temvorax.domain.BlocoRotina
import com.brunnakampferd.temvorax.domain.ItemAgenda
import com.brunnakampferd.temvorax.domain.JanelaLivre
import com.brunnakampferd.temvorax.domain.TipoItemAgenda
import com.brunnakampferd.temvorax.viewmodel.EstadoAgendaHoje
import com.brunnakampferd.temvorax.ui.academicoViewModel
import com.brunnakampferd.temvorax.ui.agendaDoDiaViewModel
import com.brunnakampferd.temvorax.ui.components.AbaPrincipal
import com.brunnakampferd.temvorax.ui.components.CartaoItemGenerico
import com.brunnakampferd.temvorax.ui.components.CartaoTarefa
import com.brunnakampferd.temvorax.ui.components.CheckboxComReforco
import com.brunnakampferd.temvorax.ui.components.MenuLateralAreas
import com.brunnakampferd.temvorax.ui.components.MenuPerfil
import com.brunnakampferd.temvorax.ui.components.NavegacaoPrincipal
import com.brunnakampferd.temvorax.ui.components.corDaArea
import com.brunnakampferd.temvorax.ui.components.form.CampoSelecao
import com.brunnakampferd.temvorax.ui.components.form.CampoTexto
import com.brunnakampferd.temvorax.ui.pessoalViewModel
import com.brunnakampferd.temvorax.ui.perfilViewModel
import com.brunnakampferd.temvorax.ui.profissionalViewModel
import com.brunnakampferd.temvorax.ui.progressoViewModel
import com.brunnakampferd.temvorax.ui.tarefaViewModel
import com.brunnakampferd.temvorax.ui.theme.CremeCard
import com.brunnakampferd.temvorax.ui.theme.CremeClaro
import com.brunnakampferd.temvorax.ui.theme.RoxoPrimario
import com.brunnakampferd.temvorax.ui.theme.TealPrimario
import com.brunnakampferd.temvorax.ui.theme.TextoPrincipal
import com.brunnakampferd.temvorax.ui.theme.TextoSecundario
import com.brunnakampferd.temvorax.ui.theme.TextoSobreRoxo
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/**
 * Home do Temvorax — a "casca" do app inteiro depois do login: cabeçalho
 * (menu de áreas à esquerda, saudação, perfil à direita) e navegação
 * principal (Hoje / Pendências / Concluído) ficam fixos aqui.
 *
 * As tarefas vêm do Room (via [com.brunnakampferd.temvorax.viewmodel.TarefaViewModel])
 * — a lista começa vazia, o usuário cria tudo pelo botão "+".
 *
 * - **Hoje**: só as tarefas com data de hoje.
 * - **Pendências**: TUDO que não está concluído, sem filtro de data.
 * - **Concluído**: tudo que já foi marcado como feito.
 *
 * O filtro de área do menu ☰ (Todas/Acadêmica/Profissional/Pessoal) é global
 * pras três abas. Escolher uma área de verdade (não "Todas") navega pro hub
 * daquela área via [onAreaSelecionada] — sair do hub volta pra cá.
 *
 * @param onAreaSelecionada chamado quando o usuário escolhe uma área (não "Todas") no menu ☰
 * @param onSairClick chamado ao tocar em "Sair da conta" no menu de Perfil
 * @param abrirMenuAreasInicial `true` quando se volta do hub de uma área — reabre o
 *   drawer "Escolha sua área" em vez de só cair na aba Hoje com o drawer fechado
 * @param onMenuAreasAberto chamado assim que o drawer é reaberto por [abrirMenuAreasInicial],
 *   pra consumir o flag e não reabrir de novo numa recomposição futura
 */
@Composable
fun HomeScreen(
    nomeUsuario: String = "Explorador",
    onAreaSelecionada: (AreaTarefa) -> Unit = {},
    onSairClick: () -> Unit = {},
    onTermosClick: () -> Unit = {},
    onPoliticaClick: () -> Unit = {},
    abrirMenuAreasInicial: Boolean = false,
    onMenuAreasAberto: () -> Unit = {}
) {
    val tarefaVm = tarefaViewModel()
    val perfilVm = perfilViewModel()
    val progressoVm = progressoViewModel()
    // Os 3 ViewModels de área só entram aqui pra rotear o toque de "concluir"
    // de um item da agenda pro dono certo (ver aoAlternarItemAgenda) — a
    // leitura da agenda em si já vem pronta do agendaDoDiaViewModel.
    val academicoVm = academicoViewModel()
    val profissionalVm = profissionalViewModel()
    val pessoalVm = pessoalViewModel()
    val agendaVm = agendaDoDiaViewModel()
    val tarefas by tarefaVm.tarefas.collectAsState()
    val perfil by perfilVm.perfil.collectAsState()
    val envioFoto by perfilVm.envioFoto.collectAsState()
    val streak by progressoVm.streak.collectAsState()
    val estadoHoje by agendaVm.estado.collectAsState()

    var abaSelecionada by remember { mutableStateOf(AbaPrincipal.HOJE) }
    var areaSelecionada by remember { mutableStateOf<AreaTarefa?>(null) }
    var mostrarDialogoAdicionar by remember { mutableStateOf(false) }

    val hoje = remember { LocalDate.now() }
    val drawerAreas = rememberDrawerState(DrawerValue.Closed)
    val drawerPerfil = rememberDrawerState(DrawerValue.Closed)
    val escopo = rememberCoroutineScope()

    LaunchedEffect(abrirMenuAreasInicial) {
        if (abrirMenuAreasInicial) {
            drawerAreas.open()
            onMenuAreasAberto()
        }
    }

    // Sem isso, o botão/gesto de voltar do sistema não fecha o drawer aberto —
    // ele "vaza" pra fora (a Home é a raiz da navegação depois do login) e
    // fecha o app inteiro em vez de só fechar o menu.
    BackHandler(enabled = drawerPerfil.isOpen) { escopo.launch { drawerPerfil.close() } }
    BackHandler(enabled = drawerAreas.isOpen) { escopo.launch { drawerAreas.close() } }

    val tarefasDaAreaEscolhida = if (areaSelecionada == null) tarefas else tarefas.filter { it.area == areaSelecionada }
    val tarefasDaAba = when (abaSelecionada) {
        AbaPrincipal.HOJE -> emptyList() // Hoje usa a agenda unificada (ver agendaFiltrada), não essa lista
        AbaPrincipal.PENDENCIAS -> tarefasDaAreaEscolhida.filter { !it.concluida }
        AbaPrincipal.CONCLUIDO -> tarefasDaAreaEscolhida.filter { it.concluida }
    }

    // Mesmo filtro de área do menu ☰ aplicado à agenda unificada. Janelas
    // livres cruzam áreas por natureza (ex.: entre o fim do expediente e o
    // início de uma aula) — não fazem sentido filtradas por uma área só,
    // então somem quando um filtro está ativo.
    val estadoFiltrado = if (areaSelecionada == null) estadoHoje else EstadoAgendaHoje(
        blocos = estadoHoje.blocos.filter { it.area == areaSelecionada },
        janelas = emptyList(),
        tarefasSemHorario = estadoHoje.tarefasSemHorario.filter { it.area == areaSelecionada }
    )

    // Concluir um item da agenda chama o ViewModel dono daquele tipo — reaproveita
    // a lógica (inclusive o registro de streak) que já existe em cada um. Aula,
    // Prova, Expediente e Compromissos não têm "concluído" — toque neles não faz nada.
    val aoAlternarItemAgenda: (ItemAgenda) -> Unit = { item ->
        when (item.tipo) {
            TipoItemAgenda.TAREFA_RAPIDA -> tarefaVm.alternarConcluida(item.origem as TarefaDia)
            TipoItemAgenda.TAREFA_DIARIA -> pessoalVm.alternarTarefaDiaria(item.origem as TarefaDiaria)
            TipoItemAgenda.TRABALHO -> academicoVm.alternarTrabalho(item.origem as TrabalhoAcademico)
            TipoItemAgenda.ATIVIDADE_PROFISSIONAL -> profissionalVm.alternarAtividade(item.origem as AtividadeProfissional)
            TipoItemAgenda.AULA, TipoItemAgenda.PROVA, TipoItemAgenda.EXPEDIENTE,
            TipoItemAgenda.COMPROMISSO_PROFISSIONAL, TipoItemAgenda.COMPROMISSO_PESSOAL -> Unit
        }
    }

    // Drawer de Perfil abre da DIREITA (flip de layout direction só nesse nível);
    // drawer de Áreas abre da ESQUERDA, normal, por dentro.
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            drawerState = drawerPerfil,
            drawerContent = {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    MenuPerfil(
                        perfil = perfil ?: Perfil(),
                        envioFoto = envioFoto,
                        onEscolherFoto = { uri -> perfilVm.trocarFoto(uri) },
                        onSalvar = { perfilVm.salvar(it) },
                        onTermosClick = onTermosClick,
                        onPoliticaClick = onPoliticaClick,
                        onSairClick = {
                            escopo.launch { drawerPerfil.close() }
                            onSairClick()
                        },
                        onExcluirConta = { /* confirmação apenas, por enquanto — ver comentário no MenuPerfil */ }
                    )
                }
            }
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                ModalNavigationDrawer(
                    drawerState = drawerAreas,
                    drawerContent = {
                        MenuLateralAreas(
                            areaSelecionada = areaSelecionada,
                            aoSelecionarArea = { area ->
                                escopo.launch { drawerAreas.close() }
                                if (area == null) {
                                    areaSelecionada = null
                                } else {
                                    onAreaSelecionada(area)
                                }
                            }
                        )
                    }
                ) {
                    Scaffold(
                        containerColor = CremeClaro,
                        topBar = {
                            CabecalhoHome(
                                nomeUsuario = nomeUsuario,
                                fotoUrl = perfil?.fotoUrl,
                                streakAtual = streak.atual,
                                onMenuClick = { escopo.launch { drawerAreas.open() } },
                                onPerfilClick = { escopo.launch { drawerPerfil.open() } }
                            )
                        },
                        bottomBar = {
                            NavegacaoPrincipal(
                                abaSelecionada = abaSelecionada,
                                aoSelecionar = { abaSelecionada = it }
                            )
                        },
                        floatingActionButton = {
                            FloatingActionButton(
                                onClick = { mostrarDialogoAdicionar = true },
                                containerColor = RoxoPrimario,
                                contentColor = TextoSobreRoxo
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = "Adicionar tarefa")
                            }
                        }
                    ) { paddingInterno ->
                        if (abaSelecionada == AbaPrincipal.HOJE) {
                            ConteudoHoje(
                                modifier = Modifier.padding(paddingInterno),
                                hoje = hoje,
                                estado = estadoFiltrado,
                                aoAlternarItem = aoAlternarItemAgenda
                            )
                        } else {
                            ConteudoAba(
                                modifier = Modifier.padding(paddingInterno),
                                aba = abaSelecionada,
                                hoje = hoje,
                                tarefas = tarefasDaAba,
                                aoAlternarTarefa = { tarefaVm.alternarConcluida(it) },
                                aoExcluirTarefa = { tarefaVm.remover(it) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogoAdicionar) {
        DialogoAdicionarTarefa(
            areaPadrao = areaSelecionada ?: AreaTarefa.PESSOAL,
            onConfirmar = { titulo, area ->
                tarefaVm.adicionar(titulo, area, hoje)
                mostrarDialogoAdicionar = false
            },
            onCancelar = { mostrarDialogoAdicionar = false }
        )
    }
}

@Composable
private fun CabecalhoHome(
    nomeUsuario: String,
    fotoUrl: String?,
    streakAtual: Int,
    onMenuClick: () -> Unit,
    onPerfilClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onMenuClick) {
            Icon(Icons.Filled.Menu, contentDescription = "Abrir menu de áreas", tint = TextoPrincipal)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "TEMVORAX",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                color = RoxoPrimario
            )
            Text(
                text = "Olá, $nomeUsuario",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextoPrincipal
            )
            // Anti-culpa: o chip só aparece com streak valendo (>= 1 dia). Se a
            // sequência quebrou, não tem "0 dias"/"sequência perdida" nenhum
            // aqui — o chip simplesmente some, sem chamar atenção pra quebra.
            if (streakAtual >= 1) {
                Spacer(Modifier.height(4.dp))
                ChipStreak(dias = streakAtual)
            }
        }

        // Reseta sempre que a URL muda, pra uma foto nova ter chance de carregar
        // mesmo que a anterior tenha falhado (ex.: objeto apagado fora do app).
        var falhaAoCarregarFoto by remember(fotoUrl) { mutableStateOf(false) }
        IconButton(onClick = onPerfilClick) {
            if (fotoUrl != null && !falhaAoCarregarFoto) {
                AsyncImage(
                    model = fotoUrl,
                    contentDescription = "Abrir perfil",
                    contentScale = ContentScale.Crop,
                    onError = { falhaAoCarregarFoto = true },
                    modifier = Modifier.size(32.dp).clip(CircleShape)
                )
            } else {
                Icon(Icons.Filled.AccountCircle, contentDescription = "Abrir perfil", tint = TextoPrincipal)
            }
        }
    }
}

/** Selinho discreto de streak — só é chamado quando há sequência valendo (ver [CabecalhoHome]). */
@Composable
private fun ChipStreak(dias: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(TealPrimario.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.LocalFireDepartment,
            contentDescription = null,
            tint = TealPrimario,
            modifier = Modifier.size(12.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = "$dias ${if (dias == 1) "dia seguido" else "dias seguidos"}",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = TealPrimario
        )
    }
}

@Composable
private fun ConteudoAba(
    aba: AbaPrincipal,
    hoje: LocalDate,
    tarefas: List<TarefaDia>,
    aoAlternarTarefa: (TarefaDia) -> Unit,
    aoExcluirTarefa: (TarefaDia) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Text(text = aba.rotulo, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextoPrincipal)

        if (aba == AbaPrincipal.HOJE) {
            Text(text = formatarDiaEData(hoje), fontSize = 14.sp, color = TextoSecundario)
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (tarefas.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = mensagemVazia(aba), color = TextoSecundario)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(items = tarefas, key = { it.id }) { tarefa ->
                    CartaoTarefa(
                        tarefa = tarefa,
                        aoAlternar = { aoAlternarTarefa(tarefa) },
                        aoExcluir = { aoExcluirTarefa(tarefa) },
                        mostrarData = aba != AbaPrincipal.HOJE,
                        hoje = hoje
                    )
                }
            }
        }
    }
}

/**
 * Aba "Hoje" — agrupada por contexto em vez de lista solta: um bloco de
 * rotina por área (Universitária/Profissional/Pessoal, cada um só aparece se
 * tiver algo pra mostrar), as janelas livres entre os compromissos com
 * horário, e por fim as tarefas sem horário definido (tarefa rápida, diária,
 * trabalho, atividade — as mesmas de sempre, concluíveis com checkbox).
 */
@Composable
private fun ConteudoHoje(
    hoje: LocalDate,
    estado: EstadoAgendaHoje,
    aoAlternarItem: (ItemAgenda) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Text(text = "Hoje", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextoPrincipal)
        Text(text = formatarDiaEData(hoje), fontSize = 14.sp, color = TextoSecundario)

        Spacer(modifier = Modifier.height(16.dp))

        if (estado.blocos.isEmpty() && estado.janelas.isEmpty() && estado.tarefasSemHorario.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Nada marcado pra hoje. Toque em + pra adicionar. 🌿", color = TextoSecundario)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (estado.blocos.isNotEmpty()) {
                    items(items = estado.blocos, key = { "bloco_${it.titulo}" }) { bloco ->
                        CartaoBlocoRotina(bloco)
                    }
                }
                if (estado.janelas.isNotEmpty()) {
                    item(key = "titulo_janelas") { TituloSecaoAgenda("Janelas livres") }
                    items(items = estado.janelas, key = { "janela_${it.inicio}_${it.duracaoMinutos}" }) { janela ->
                        CartaoJanelaLivre(janela)
                    }
                }
                if (estado.tarefasSemHorario.isNotEmpty()) {
                    item(key = "titulo_tarefas") { TituloSecaoAgenda("Tarefas de hoje") }
                    items(items = estado.tarefasSemHorario, key = { "tarefa_${chaveItemAgenda(it)}" }) { item ->
                        CartaoItemAgenda(item = item, aoAlternar = aoAlternarItem)
                    }
                }
            }
        }
    }
}

/** Um bloco de rotina (ver [BlocoRotina]) — contexto fixo + o próximo item com horário daquela área, se houver. */
@Composable
private fun CartaoBlocoRotina(bloco: BlocoRotina) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(14.dp), clip = false)
            .background(CremeCard, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Text(bloco.titulo, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextoPrincipal)
        if (bloco.linhaContexto != null) {
            Spacer(Modifier.height(2.dp))
            Text(bloco.linhaContexto, fontSize = 13.sp, color = TextoSecundario)
        }
        if (bloco.proximoItem != null) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = "${bloco.proximoItem.horaInicio} • ${bloco.proximoItem.titulo}",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = corDaArea(bloco.area)
            )
        }
    }
}

/** Ex.: "45min às 18:00 • depois de Expediente" (ver [JanelaLivre]). */
@Composable
private fun CartaoJanelaLivre(janela: JanelaLivre) {
    CartaoItemGenerico(
        titulo = "Janela livre",
        subtitulo = "${formatarDuracaoMinutos(janela.duracaoMinutos)} às ${janela.inicio} • depois de ${janela.apos.titulo}",
        corLateral = TextoSecundario
    )
}

private fun formatarDuracaoMinutos(minutos: Long): String {
    val horas = minutos / 60
    val resto = minutos % 60
    return when {
        horas > 0 && resto > 0 -> "${horas}h${resto}min"
        horas > 0 -> "${horas}h"
        else -> "${minutos}min"
    }
}

@Composable
private fun TituloSecaoAgenda(texto: String) {
    Text(
        text = texto,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextoSecundario,
        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
    )
}

/**
 * Card de um item da agenda: concluíveis (Tarefa, Tarefa diária, Trabalho,
 * Atividade) ganham checkbox; Aula/Prova/Expediente/Compromisso aparecem só
 * como informação, sem nenhuma ação ao tocar — decisão de produto pra essa
 * primeira versão da agenda unificada.
 */
@Composable
private fun CartaoItemAgenda(item: ItemAgenda, aoAlternar: (ItemAgenda) -> Unit) {
    CartaoItemGenerico(
        titulo = item.titulo,
        subtitulo = buildString {
            append(rotuloTipoAgenda(item.tipo))
            if (item.horaInicio != null && item.horaFim != null) {
                append(" • ${item.horaInicio}–${item.horaFim}")
            }
        },
        corLateral = corDaArea(item.area),
        trailing = if (item.concluida != null) {
            { CheckboxComReforco(checked = item.concluida, onCheckedChange = { aoAlternar(item) }) }
        } else null
    )
}

private fun rotuloTipoAgenda(tipo: TipoItemAgenda): String = when (tipo) {
    TipoItemAgenda.TAREFA_RAPIDA -> "Tarefa"
    TipoItemAgenda.TAREFA_DIARIA -> "Tarefa diária"
    TipoItemAgenda.AULA -> "Aula"
    TipoItemAgenda.PROVA -> "Prova"
    TipoItemAgenda.TRABALHO -> "Trabalho"
    TipoItemAgenda.EXPEDIENTE -> "Expediente"
    TipoItemAgenda.COMPROMISSO_PROFISSIONAL, TipoItemAgenda.COMPROMISSO_PESSOAL -> "Compromisso"
    TipoItemAgenda.ATIVIDADE_PROFISSIONAL -> "Atividade"
}

/** Chave estável pro LazyColumn — o id vem da entidade original guardada em [ItemAgenda.origem]. */
private fun chaveItemAgenda(item: ItemAgenda): String {
    val id = when (item.tipo) {
        TipoItemAgenda.TAREFA_RAPIDA -> (item.origem as TarefaDia).id
        TipoItemAgenda.TAREFA_DIARIA -> (item.origem as TarefaDiaria).id
        TipoItemAgenda.AULA -> (item.origem as Disciplina).id
        TipoItemAgenda.PROVA -> (item.origem as Prova).id
        TipoItemAgenda.TRABALHO -> (item.origem as TrabalhoAcademico).id
        TipoItemAgenda.EXPEDIENTE -> (item.origem as Expediente).id
        TipoItemAgenda.COMPROMISSO_PROFISSIONAL -> (item.origem as CompromissoProfissional).id
        TipoItemAgenda.COMPROMISSO_PESSOAL -> (item.origem as CompromissoPessoal).id
        TipoItemAgenda.ATIVIDADE_PROFISSIONAL -> (item.origem as AtividadeProfissional).id
    }
    return "${item.tipo}_$id"
}

@Composable
private fun DialogoAdicionarTarefa(
    areaPadrao: AreaTarefa,
    onConfirmar: (String, AreaTarefa) -> Unit,
    onCancelar: () -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var area by remember { mutableStateOf(areaPadrao) }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Nova tarefa") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                CampoTexto(valor = titulo, aoMudar = { titulo = it }, rotulo = "O que você precisa fazer?")
                CampoSelecao(
                    valor = area,
                    opcoes = AreaTarefa.entries,
                    rotuloOpcao = { it.rotulo },
                    rotulo = "Área",
                    aoMudar = { area = it }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { if (titulo.isNotBlank()) onConfirmar(titulo, area) }) {
                Text("Adicionar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}

private fun mensagemVazia(aba: AbaPrincipal): String = when (aba) {
    AbaPrincipal.HOJE -> "Nada marcado pra hoje. Toque em + pra adicionar. 🌿"
    AbaPrincipal.PENDENCIAS -> "Nenhuma pendência — tudo em dia! 🎉"
    AbaPrincipal.CONCLUIDO -> "Ainda não tem nada concluído por aqui."
}

/** Ex.: "Quinta-feira, 06/08" */
private fun formatarDiaEData(data: LocalDate): String {
    val diaSemana = data.dayOfWeek
        .getDisplayName(TextStyle.FULL, Locale.forLanguageTag("pt-BR"))
        .replaceFirstChar { it.uppercase() }
    val dataCurta = data.format(DateTimeFormatter.ofPattern("dd/MM"))
    return "$diaSemana, $dataCurta"
}
