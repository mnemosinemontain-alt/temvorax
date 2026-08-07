package com.brunnakampferd.temvorax.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.brunnakampferd.temvorax.data.model.AreaTarefa
import com.brunnakampferd.temvorax.data.model.RegistroFrequencia
import com.brunnakampferd.temvorax.data.model.TipoBolsaEdital
import com.brunnakampferd.temvorax.ui.academicoViewModel
import com.brunnakampferd.temvorax.ui.pessoalViewModel
import com.brunnakampferd.temvorax.ui.planejamentoViewModel
import com.brunnakampferd.temvorax.ui.profissionalViewModel
import com.brunnakampferd.temvorax.ui.screens.SplashScreen
import com.brunnakampferd.temvorax.ui.screens.WelcomeScreen
import com.brunnakampferd.temvorax.ui.screens.academico.*
import com.brunnakampferd.temvorax.ui.screens.auth.AuthScreen
import com.brunnakampferd.temvorax.ui.screens.home.HomeScreen
import com.brunnakampferd.temvorax.ui.screens.hub.AreaHubScreen
import com.brunnakampferd.temvorax.ui.screens.hub.itensHubAcademica
import com.brunnakampferd.temvorax.ui.screens.hub.itensHubPessoal
import com.brunnakampferd.temvorax.ui.screens.hub.itensHubProfissional
import com.brunnakampferd.temvorax.ui.screens.legal.POLITICA_DE_PRIVACIDADE
import com.brunnakampferd.temvorax.ui.screens.legal.TERMOS_DE_USO
import com.brunnakampferd.temvorax.ui.screens.legal.TextoLegalScreen
import com.brunnakampferd.temvorax.ui.screens.pessoal.*
import com.brunnakampferd.temvorax.ui.screens.profissional.*
import com.brunnakampferd.temvorax.viewmodel.AuthUiState
import com.brunnakampferd.temvorax.viewmodel.AuthViewModel

/**
 * Grafo de navegação inteiro do Temvorax. Fica separado do MainActivity pra
 * esse não virar um arquivo gigante — o MainActivity só monta as dependências
 * que precisam de Activity de verdade (Google Sign-In) e entrega pra cá.
 */
@Composable
fun TemvoraxNavHost(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    startDestination: String,
    onGoogleSignInClick: () -> Unit
) {
    NavHost(navController = navController, startDestination = startDestination) {

        // ---------------- Autenticação ----------------
        composable(Rotas.SPLASH) {
            SplashScreen(onContinue = {
                navController.navigate(Rotas.WELCOME) { popUpTo(Rotas.SPLASH) { inclusive = true } }
            })
        }
        composable(Rotas.WELCOME) {
            // "Entrar com Google" dispara o seletor de conta direto daqui — sem
            // passar pela tela de e-mail/senha. Por isso precisa observar o
            // resultado aqui também (a AuthScreen faz o mesmo, mas ela nem
            // entra em cena nesse fluxo).
            val uiState by authViewModel.uiState.collectAsState()
            LaunchedEffect(uiState) {
                if (uiState is AuthUiState.Sucesso) {
                    navController.navigate(Rotas.HOME) { popUpTo(0) { inclusive = true } }
                }
            }
            WelcomeScreen(
                onEntrarComGoogleClick = onGoogleSignInClick,
                onCriarContaClick = { navController.navigate(Rotas.CADASTRO) },
                onJaTenhoContaClick = { navController.navigate(Rotas.LOGIN) },
                onTermosClick = { navController.navigate(Rotas.TERMOS) },
                onPoliticaClick = { navController.navigate(Rotas.PRIVACIDADE) }
            )
        }
        composable(Rotas.LOGIN) {
            AuthScreen(
                modoInicial = true,
                viewModel = authViewModel,
                onAutenticado = { navController.navigate(Rotas.HOME) { popUpTo(0) { inclusive = true } } },
                onVoltarClick = { navController.popBackStack() },
                onTermosClick = { navController.navigate(Rotas.TERMOS) },
                onPoliticaClick = { navController.navigate(Rotas.PRIVACIDADE) }
            )
        }
        composable(Rotas.CADASTRO) {
            AuthScreen(
                modoInicial = false,
                viewModel = authViewModel,
                onAutenticado = { navController.navigate(Rotas.HOME) { popUpTo(0) { inclusive = true } } },
                onVoltarClick = { navController.popBackStack() },
                onTermosClick = { navController.navigate(Rotas.TERMOS) },
                onPoliticaClick = { navController.navigate(Rotas.PRIVACIDADE) }
            )
        }
        composable(Rotas.TERMOS) {
            TextoLegalScreen(titulo = "Termos de Uso", conteudo = TERMOS_DE_USO, onVoltarClick = { navController.popBackStack() })
        }
        composable(Rotas.PRIVACIDADE) {
            TextoLegalScreen(titulo = "Política de Privacidade", conteudo = POLITICA_DE_PRIVACIDADE, onVoltarClick = { navController.popBackStack() })
        }

        // ---------------- Home ----------------
        composable(Rotas.HOME) { backStackEntry ->
            val abrirMenuAreas by backStackEntry.savedStateHandle
                .getStateFlow(Rotas.ABRIR_MENU_AREAS, false)
                .collectAsState()
            HomeScreen(
                nomeUsuario = authViewModel.nomeUsuarioAtual(),
                abrirMenuAreasInicial = abrirMenuAreas,
                onMenuAreasAberto = { backStackEntry.savedStateHandle[Rotas.ABRIR_MENU_AREAS] = false },
                onAreaSelecionada = { area -> navController.navigate(Rotas.areaHub(area.name)) },
                onSairClick = {
                    authViewModel.sair()
                    navController.navigate(Rotas.SPLASH) { popUpTo(0) { inclusive = true } }
                },
                onTermosClick = { navController.navigate(Rotas.TERMOS) },
                onPoliticaClick = { navController.navigate(Rotas.PRIVACIDADE) }
            )
        }

        composable(
            route = Rotas.AREA_HUB,
            arguments = listOf(navArgument("area") { type = NavType.StringType })
        ) { backStackEntry ->
            val area = AreaTarefa.valueOf(backStackEntry.arguments?.getString("area") ?: AreaTarefa.ACADEMICA.name)
            val itens = when (area) {
                AreaTarefa.ACADEMICA -> itensHubAcademica()
                AreaTarefa.PROFISSIONAL -> itensHubProfissional()
                AreaTarefa.PESSOAL -> itensHubPessoal()
            }
            // Sair do hub de uma área deve reabrir o drawer "Escolha sua área" na
            // Home, não só cair na aba Hoje — vale tanto pra seta do topo quanto
            // pro botão/gesto de voltar do sistema, por isso o BackHandler aqui.
            val voltarParaEscolhaDeArea: () -> Unit = {
                navController.previousBackStackEntry?.savedStateHandle?.set(Rotas.ABRIR_MENU_AREAS, true)
                navController.popBackStack()
            }
            BackHandler(onBack = voltarParaEscolhaDeArea)
            AreaHubScreen(
                area = area,
                itens = itens,
                onVoltar = voltarParaEscolhaDeArea,
                onItemClick = { item -> navController.navigate(item.rota) }
            )
        }

        // ---------------- Acadêmica: Semestres ----------------
        composable(Rotas.SEMESTRES) {
            val vm = academicoViewModel()
            val semestres by vm.semestres.collectAsState()
            SemestresScreen(
                semestres = semestres,
                onVoltar = { navController.popBackStack() },
                onAdicionar = { navController.navigate(Rotas.semestreForm(0)) },
                onEditar = { navController.navigate(Rotas.semestreForm(it.id)) },
                onExcluir = { vm.removerSemestre(it) },
                onAbrirDetalhe = { navController.navigate(Rotas.semestreDetalhe(it.id)) }
            )
        }
        composable(
            route = Rotas.SEMESTRE_FORM,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val vm = academicoViewModel()
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            val semestres by vm.semestres.collectAsState()
            val semestre = semestres.find { it.id == id }
            SemestreFormScreen(
                semestre = semestre,
                onVoltar = { navController.popBackStack() },
                onSalvar = { salvo ->
                    val eraNovo = salvo.id == 0L
                    vm.salvarSemestre(salvo) { idSalvo ->
                        if (eraNovo) {
                            // Próximo passo natural depois de CRIAR um semestre é
                            // adicionar as disciplinas dele — vai direto pro detalhe
                            // (aba Disciplinas, com o "+" logo ali) em vez de deixar o
                            // usuário procurar isso de volta na lista de semestres.
                            navController.navigate(Rotas.semestreDetalhe(idSalvo)) {
                                popUpTo(Rotas.SEMESTRES) { inclusive = false }
                            }
                        } else {
                            navController.popBackStack()
                        }
                    }
                },
                onExcluir = semestre?.let { s -> { vm.removerSemestre(s); navController.popBackStack() } }
            )
        }
        composable(
            route = Rotas.SEMESTRE_DETALHE,
            arguments = listOf(navArgument("semestreId") { type = NavType.LongType })
        ) { backStackEntry ->
            val vm = academicoViewModel()
            val semestreId = backStackEntry.arguments?.getLong("semestreId") ?: 0L
            val semestres by vm.semestres.collectAsState()
            val semestre = semestres.find { it.id == semestreId }
            val disciplinas by vm.observarDisciplinas(semestreId).collectAsState(initial = emptyList())
            val provas by vm.observarProvas(semestreId).collectAsState(initial = emptyList())
            val trabalhos by vm.observarTrabalhos(semestreId).collectAsState(initial = emptyList())

            SemestreDetalheScreen(
                semestre = semestre,
                disciplinas = disciplinas,
                provas = provas,
                trabalhos = trabalhos,
                onVoltar = { navController.popBackStack() },
                onAdicionarDisciplina = { navController.navigate(Rotas.disciplinaForm(semestreId, 0)) },
                onEditarDisciplina = { navController.navigate(Rotas.disciplinaForm(semestreId, it.id)) },
                onExcluirDisciplina = { vm.removerDisciplina(it) },
                onAdicionarProva = { navController.navigate(Rotas.provaForm(semestreId, 0)) },
                onEditarProva = { navController.navigate(Rotas.provaForm(semestreId, it.id)) },
                onExcluirProva = { vm.removerProva(it) },
                onAdicionarTrabalho = { navController.navigate(Rotas.trabalhoForm(semestreId, 0)) },
                onEditarTrabalho = { navController.navigate(Rotas.trabalhoForm(semestreId, it.id)) },
                onAlternarTrabalho = { vm.alternarTrabalho(it) },
                onExcluirTrabalho = { vm.removerTrabalho(it) },
                onAbrirFrequencia = { navController.navigate(Rotas.frequencia(semestreId, it.id)) }
            )
        }
        composable(
            route = Rotas.DISCIPLINA_FORM,
            arguments = listOf(
                navArgument("semestreId") { type = NavType.LongType },
                navArgument("id") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val vm = academicoViewModel()
            val semestreId = backStackEntry.arguments?.getLong("semestreId") ?: 0L
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            val disciplinas by vm.observarDisciplinas(semestreId).collectAsState(initial = emptyList())
            val disciplina = disciplinas.find { it.id == id }
            DisciplinaFormScreen(
                semestreId = semestreId,
                disciplina = disciplina,
                onVoltar = { navController.popBackStack() },
                onSalvar = { vm.salvarDisciplina(it); navController.popBackStack() },
                onExcluir = disciplina?.let { d -> { vm.removerDisciplina(d); navController.popBackStack() } }
            )
        }
        composable(
            route = Rotas.PROVA_FORM,
            arguments = listOf(
                navArgument("semestreId") { type = NavType.LongType },
                navArgument("id") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val vm = academicoViewModel()
            val semestreId = backStackEntry.arguments?.getLong("semestreId") ?: 0L
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            val provas by vm.observarProvas(semestreId).collectAsState(initial = emptyList())
            val disciplinas by vm.observarDisciplinas(semestreId).collectAsState(initial = emptyList())
            val prova = provas.find { it.id == id }
            ProvaFormScreen(
                semestreId = semestreId,
                prova = prova,
                disciplinasDoSemestre = disciplinas,
                onVoltar = { navController.popBackStack() },
                onSalvar = { vm.salvarProva(it); navController.popBackStack() },
                onExcluir = prova?.let { p -> { vm.removerProva(p); navController.popBackStack() } }
            )
        }
        composable(
            route = Rotas.TRABALHO_FORM,
            arguments = listOf(
                navArgument("semestreId") { type = NavType.LongType },
                navArgument("id") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val vm = academicoViewModel()
            val semestreId = backStackEntry.arguments?.getLong("semestreId") ?: 0L
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            val trabalhos by vm.observarTrabalhos(semestreId).collectAsState(initial = emptyList())
            val disciplinas by vm.observarDisciplinas(semestreId).collectAsState(initial = emptyList())
            val trabalho = trabalhos.find { it.id == id }
            TrabalhoFormScreen(
                semestreId = semestreId,
                trabalho = trabalho,
                disciplinasDoSemestre = disciplinas,
                onVoltar = { navController.popBackStack() },
                onSalvar = { vm.salvarTrabalho(it); navController.popBackStack() },
                onExcluir = trabalho?.let { t -> { vm.removerTrabalho(t); navController.popBackStack() } }
            )
        }
        composable(
            route = Rotas.FREQUENCIA,
            arguments = listOf(
                navArgument("semestreId") { type = NavType.LongType },
                navArgument("disciplinaId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val vm = academicoViewModel()
            val semestreId = backStackEntry.arguments?.getLong("semestreId") ?: 0L
            val disciplinaId = backStackEntry.arguments?.getLong("disciplinaId") ?: 0L
            val disciplinas by vm.observarDisciplinas(semestreId).collectAsState(initial = emptyList())
            val disciplina = disciplinas.find { it.id == disciplinaId }
            val registros by vm.observarFrequencia(disciplinaId).collectAsState(initial = emptyList())
            FrequenciaScreen(
                disciplina = disciplina,
                registros = registros,
                onVoltar = { navController.popBackStack() },
                onAdicionar = { data, presente ->
                    vm.salvarFrequencia(RegistroFrequencia(disciplinaId = disciplinaId, data = data, presente = presente))
                },
                onExcluir = { vm.removerFrequencia(it) }
            )
        }

        composable(Rotas.PLANEJAMENTO) {
            val vm = planejamentoViewModel()
            val resultado by vm.resultado.collectAsState()
            val carregando by vm.carregando.collectAsState()
            LaunchedEffect(Unit) { vm.carregar() }
            PlanejamentoScreen(resultado = resultado, carregando = carregando, onVoltar = { navController.popBackStack() })
        }

        composable(Rotas.CALENDARIO) {
            val vm = academicoViewModel()
            val eventos by vm.calendario.collectAsState()
            val sincronizacao by vm.sincronizacaoCalendario.collectAsState()
            CalendarioAcademicoScreen(
                eventos = eventos,
                onVoltar = { navController.popBackStack() },
                onAdicionar = { navController.navigate(Rotas.calendarioForm(0)) },
                onEditar = { navController.navigate(Rotas.calendarioForm(it.id)) },
                onExcluir = { vm.removerEventoCalendario(it) },
                sincronizacao = sincronizacao,
                onObterCalendarios = { contexto -> vm.calendariosDoDispositivo(contexto) },
                onSincronizar = { contexto, calendarioId -> vm.sincronizarCalendarioComDispositivo(contexto, calendarioId) },
                onSincronizacaoConsumida = { vm.resetarSincronizacaoCalendario() }
            )
        }
        composable(
            route = Rotas.CALENDARIO_FORM,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val vm = academicoViewModel()
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            val eventos by vm.calendario.collectAsState()
            val evento = eventos.find { it.id == id }
            EventoCalendarioFormScreen(
                evento = evento,
                onVoltar = { navController.popBackStack() },
                onSalvar = { vm.salvarEventoCalendario(it); navController.popBackStack() },
                onExcluir = evento?.let { e -> { vm.removerEventoCalendario(e); navController.popBackStack() } }
            )
        }

        composable(Rotas.BOLSAS) {
            val vm = academicoViewModel()
            val bolsas by vm.bolsas.collectAsState()
            BolsasEditaisScreen(
                bolsas = bolsas,
                onVoltar = { navController.popBackStack() },
                onAdicionar = { tipo -> navController.navigate(Rotas.bolsaForm(0, tipo.name)) },
                onEditar = { navController.navigate(Rotas.bolsaForm(it.id, it.tipo.name)) },
                onExcluir = { vm.removerBolsa(it) }
            )
        }
        composable(
            route = Rotas.BOLSA_FORM,
            arguments = listOf(
                navArgument("id") { type = NavType.LongType },
                navArgument("tipo") { type = NavType.StringType; defaultValue = TipoBolsaEdital.BOLSA.name }
            )
        ) { backStackEntry ->
            val vm = academicoViewModel()
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            val tipoPadrao = backStackEntry.arguments?.getString("tipo")
                ?.let { runCatching { TipoBolsaEdital.valueOf(it) }.getOrNull() }
                ?: TipoBolsaEdital.BOLSA
            val bolsas by vm.bolsas.collectAsState()
            val bolsa = bolsas.find { it.id == id }
            BolsaEditalFormScreen(
                bolsa = bolsa,
                tipoPadrao = tipoPadrao,
                onVoltar = { navController.popBackStack() },
                onSalvar = { vm.salvarBolsa(it); navController.popBackStack() },
                onExcluir = bolsa?.let { b -> { vm.removerBolsa(b); navController.popBackStack() } }
            )
        }

        composable(Rotas.HORAS) {
            val vm = academicoViewModel()
            val horas by vm.horasComplementares.collectAsState()
            HorasComplementaresScreen(
                horas = horas,
                onVoltar = { navController.popBackStack() },
                onAdicionar = { navController.navigate(Rotas.horasForm(0)) },
                onEditar = { navController.navigate(Rotas.horasForm(it.id)) },
                onExcluir = { vm.removerHora(it) }
            )
        }
        composable(
            route = Rotas.HORAS_FORM,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val vm = academicoViewModel()
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            val horas by vm.horasComplementares.collectAsState()
            val item = horas.find { it.id == id }
            HoraComplementarFormScreen(
                item = item,
                onVoltar = { navController.popBackStack() },
                onSalvar = { vm.salvarHora(it); navController.popBackStack() },
                onExcluir = item?.let { h -> { vm.removerHora(h); navController.popBackStack() } }
            )
        }

        composable(Rotas.ESTAGIO) {
            val vm = academicoViewModel()
            val estagios by vm.estagios.collectAsState()
            EstagioAcademicoScreen(
                estagios = estagios,
                onVoltar = { navController.popBackStack() },
                onAdicionar = { navController.navigate(Rotas.estagioForm(0)) },
                onEditar = { navController.navigate(Rotas.estagioForm(it.id)) },
                onExcluir = { vm.removerEstagio(it) }
            )
        }
        composable(
            route = Rotas.ESTAGIO_FORM,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val vm = academicoViewModel()
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            val estagios by vm.estagios.collectAsState()
            val estagio = estagios.find { it.id == id }
            EstagioAcademicoFormScreen(
                estagio = estagio,
                onVoltar = { navController.popBackStack() },
                onSalvar = { vm.salvarEstagio(it); navController.popBackStack() },
                onExcluir = estagio?.let { e -> { vm.removerEstagio(e); navController.popBackStack() } }
            )
        }

        composable(Rotas.TCC) {
            val vm = academicoViewModel()
            val tccs by vm.tccs.collectAsState()
            TccScreen(
                tccs = tccs,
                onVoltar = { navController.popBackStack() },
                onAdicionar = { navController.navigate(Rotas.tccForm(0)) },
                onEditar = { navController.navigate(Rotas.tccForm(it.id)) },
                onExcluir = { vm.removerTcc(it) }
            )
        }
        composable(
            route = Rotas.TCC_FORM,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val vm = academicoViewModel()
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            val tccs by vm.tccs.collectAsState()
            val tcc = tccs.find { it.id == id }
            val anexos by vm.observarAnexosTcc(id).collectAsState(initial = emptyList())
            val envioAnexo by vm.envioAnexo.collectAsState()
            TccFormScreen(
                tcc = tcc,
                anexos = anexos,
                envioAnexo = envioAnexo,
                onVoltar = { navController.popBackStack() },
                onSalvar = { vm.salvarTcc(it); navController.popBackStack() },
                onExcluir = tcc?.let { t -> { vm.removerTcc(t); navController.popBackStack() } },
                onAnexarArquivo = { uri, nome -> vm.anexarArquivoTcc(id, uri, nome) },
                onExcluirAnexo = { vm.removerAnexoTcc(it) }
            )
        }

        // ---------------- Profissional ----------------
        composable(Rotas.EXPEDIENTE) {
            val vm = profissionalViewModel()
            val expedientes by vm.expedientes.collectAsState()
            ExpedienteScreen(
                expedientes = expedientes,
                onVoltar = { navController.popBackStack() },
                onAdicionar = { navController.navigate(Rotas.expedienteForm(0)) },
                onEditar = { navController.navigate(Rotas.expedienteForm(it.id)) },
                onExcluir = { vm.removerExpediente(it) }
            )
        }
        composable(
            route = Rotas.EXPEDIENTE_FORM,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val vm = profissionalViewModel()
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            val expedientes by vm.expedientes.collectAsState()
            val expediente = expedientes.find { it.id == id }
            ExpedienteFormScreen(
                expediente = expediente,
                onVoltar = { navController.popBackStack() },
                onSalvar = { vm.salvarExpediente(it); navController.popBackStack() },
                onExcluir = expediente?.let { e -> { vm.removerExpediente(e); navController.popBackStack() } }
            )
        }

        composable(Rotas.COMPROMISSO_PROFISSIONAL) {
            val vm = profissionalViewModel()
            val compromissos by vm.compromissos.collectAsState()
            CompromissoProfissionalScreen(
                compromissos = compromissos,
                onVoltar = { navController.popBackStack() },
                onAdicionar = { navController.navigate(Rotas.compromissoProfissionalForm(0)) },
                onEditar = { navController.navigate(Rotas.compromissoProfissionalForm(it.id)) },
                onExcluir = { vm.removerCompromisso(it) }
            )
        }
        composable(
            route = Rotas.COMPROMISSO_PROFISSIONAL_FORM,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val vm = profissionalViewModel()
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            val compromissos by vm.compromissos.collectAsState()
            val compromisso = compromissos.find { it.id == id }
            CompromissoProfissionalFormScreen(
                compromisso = compromisso,
                onVoltar = { navController.popBackStack() },
                onSalvar = { vm.salvarCompromisso(it); navController.popBackStack() },
                onExcluir = compromisso?.let { c -> { vm.removerCompromisso(c); navController.popBackStack() } }
            )
        }

        composable(Rotas.ATIVIDADE_PROFISSIONAL) {
            val vm = profissionalViewModel()
            val atividades by vm.atividades.collectAsState()
            AtividadeProfissionalScreen(
                atividades = atividades,
                onVoltar = { navController.popBackStack() },
                onAdicionar = { navController.navigate(Rotas.atividadeProfissionalForm(0)) },
                onEditar = { navController.navigate(Rotas.atividadeProfissionalForm(it.id)) },
                onAlternar = { vm.alternarAtividade(it) },
                onExcluir = { vm.removerAtividade(it) }
            )
        }
        composable(
            route = Rotas.ATIVIDADE_PROFISSIONAL_FORM,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val vm = profissionalViewModel()
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            val atividades by vm.atividades.collectAsState()
            val atividade = atividades.find { it.id == id }
            AtividadeProfissionalFormScreen(
                atividade = atividade,
                onVoltar = { navController.popBackStack() },
                onSalvar = { vm.salvarAtividade(it); navController.popBackStack() },
                onExcluir = atividade?.let { a -> { vm.removerAtividade(a); navController.popBackStack() } }
            )
        }

        // ---------------- Pessoal ----------------
        composable(Rotas.TAREFAS_DIARIAS) {
            val vm = pessoalViewModel()
            val tarefas by vm.tarefasDiarias.collectAsState()
            TarefasDiariasScreen(
                tarefas = tarefas,
                onVoltar = { navController.popBackStack() },
                onAdicionar = { navController.navigate(Rotas.tarefaDiariaForm(0)) },
                onAlternar = { vm.alternarTarefaDiaria(it) },
                onExcluir = { vm.removerTarefaDiaria(it) }
            )
        }
        composable(
            route = Rotas.TAREFA_DIARIA_FORM,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val vm = pessoalViewModel()
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            val tarefas by vm.tarefasDiarias.collectAsState()
            val tarefa = tarefas.find { it.id == id }
            TarefaDiariaFormScreen(
                tarefa = tarefa,
                onVoltar = { navController.popBackStack() },
                onSalvar = { vm.salvarTarefaDiaria(it); navController.popBackStack() },
                onExcluir = tarefa?.let { t -> { vm.removerTarefaDiaria(t); navController.popBackStack() } }
            )
        }

        composable(Rotas.COMPROMISSO_PESSOAL) {
            val vm = pessoalViewModel()
            val compromissos by vm.compromissos.collectAsState()
            CompromissoPessoalScreen(
                compromissos = compromissos,
                onVoltar = { navController.popBackStack() },
                onAdicionar = { navController.navigate(Rotas.compromissoPessoalForm(0)) },
                onEditar = { navController.navigate(Rotas.compromissoPessoalForm(it.id)) },
                onExcluir = { vm.removerCompromisso(it) }
            )
        }
        composable(
            route = Rotas.COMPROMISSO_PESSOAL_FORM,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val vm = pessoalViewModel()
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            val compromissos by vm.compromissos.collectAsState()
            val compromisso = compromissos.find { it.id == id }
            CompromissoPessoalFormScreen(
                compromisso = compromisso,
                onVoltar = { navController.popBackStack() },
                onSalvar = { vm.salvarCompromisso(it); navController.popBackStack() },
                onExcluir = compromisso?.let { c -> { vm.removerCompromisso(c); navController.popBackStack() } }
            )
        }
    }
}
