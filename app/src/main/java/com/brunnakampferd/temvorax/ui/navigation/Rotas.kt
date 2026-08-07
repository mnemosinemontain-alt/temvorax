package com.brunnakampferd.temvorax.ui.navigation

/**
 * Todas as rotas de navegação do app, num lugar só. IDs de item nas rotas de
 * formulário usam `0` pra significar "item novo" (nada salvo ainda) — não
 * existe uma tela de formulário separada pra criar vs. editar, é a mesma
 * tela decidindo com base nesse id.
 */
object Rotas {
    const val SPLASH = "splash"
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val CADASTRO = "cadastro"
    const val TERMOS = "termos"
    const val PRIVACIDADE = "privacidade"
    const val HOME = "home"

    /** Chave do savedStateHandle usada pra avisar a Home que deve reabrir o
     * drawer "Escolha sua área" ao voltar do hub de uma área — ver
     * [TemvoraxNavHost]. */
    const val ABRIR_MENU_AREAS = "abrir_menu_areas"

    const val AREA_HUB = "area_hub/{area}"
    fun areaHub(area: String) = "area_hub/$area"

    // ---- Acadêmica ----
    const val SEMESTRES = "semestres"
    const val SEMESTRE_FORM = "semestre_form/{id}"
    fun semestreForm(id: Long) = "semestre_form/$id"
    const val SEMESTRE_DETALHE = "semestre_detalhe/{semestreId}"
    fun semestreDetalhe(id: Long) = "semestre_detalhe/$id"

    const val DISCIPLINA_FORM = "disciplina_form/{semestreId}/{id}"
    fun disciplinaForm(semestreId: Long, id: Long) = "disciplina_form/$semestreId/$id"
    const val PROVA_FORM = "prova_form/{semestreId}/{id}"
    fun provaForm(semestreId: Long, id: Long) = "prova_form/$semestreId/$id"
    const val TRABALHO_FORM = "trabalho_form/{semestreId}/{id}"
    fun trabalhoForm(semestreId: Long, id: Long) = "trabalho_form/$semestreId/$id"
    const val FREQUENCIA = "frequencia/{semestreId}/{disciplinaId}"
    fun frequencia(semestreId: Long, disciplinaId: Long) = "frequencia/$semestreId/$disciplinaId"

    const val PLANEJAMENTO = "planejamento"

    const val CALENDARIO = "calendario"
    const val CALENDARIO_FORM = "calendario_form/{id}"
    fun calendarioForm(id: Long) = "calendario_form/$id"

    const val BOLSAS = "bolsas"
    // "tipo" decide se o form abre pré-selecionado como Bolsa ou Edital — segue
    // a aba em que o usuário estava ao tocar em "+" (ver BolsasEditaisScreen).
    const val BOLSA_FORM = "bolsa_form/{id}?tipo={tipo}"
    fun bolsaForm(id: Long, tipo: String = "BOLSA") = "bolsa_form/$id?tipo=$tipo"

    const val HORAS = "horas"
    const val HORAS_FORM = "horas_form/{id}"
    fun horasForm(id: Long) = "horas_form/$id"

    const val ESTAGIO = "estagio"
    const val ESTAGIO_FORM = "estagio_form/{id}"
    fun estagioForm(id: Long) = "estagio_form/$id"

    const val TCC = "tcc"
    const val TCC_FORM = "tcc_form/{id}"
    fun tccForm(id: Long) = "tcc_form/$id"

    // ---- Profissional ----
    const val EXPEDIENTE = "expediente"
    const val EXPEDIENTE_FORM = "expediente_form/{id}"
    fun expedienteForm(id: Long) = "expediente_form/$id"

    const val COMPROMISSO_PROFISSIONAL = "compromisso_profissional"
    const val COMPROMISSO_PROFISSIONAL_FORM = "compromisso_profissional_form/{id}"
    fun compromissoProfissionalForm(id: Long) = "compromisso_profissional_form/$id"

    const val ATIVIDADE_PROFISSIONAL = "atividade_profissional"
    const val ATIVIDADE_PROFISSIONAL_FORM = "atividade_profissional_form/{id}"
    fun atividadeProfissionalForm(id: Long) = "atividade_profissional_form/$id"

    // ---- Pessoal ----
    const val TAREFAS_DIARIAS = "tarefas_diarias"
    const val TAREFA_DIARIA_FORM = "tarefa_diaria_form/{id}"
    fun tarefaDiariaForm(id: Long) = "tarefa_diaria_form/$id"

    const val COMPROMISSO_PESSOAL = "compromisso_pessoal"
    const val COMPROMISSO_PESSOAL_FORM = "compromisso_pessoal_form/{id}"
    fun compromissoPessoalForm(id: Long) = "compromisso_pessoal_form/$id"
}
