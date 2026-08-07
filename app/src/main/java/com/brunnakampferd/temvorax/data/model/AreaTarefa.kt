package com.brunnakampferd.temvorax.data.model

/**
 * As três áreas da vida que o Temvorax ajuda a organizar: estudos, trabalho
 * e vida pessoal. Toda tarefa pertence a uma dessas três — não existe uma
 * quarta opção "Todas" aqui porque isso é só um estado de FILTRO na tela
 * (representado por `null` onde a área é opcional), não uma área de verdade.
 *
 * Fica em `data/model` (sem nenhuma dependência de Compose) de propósito:
 * a cor e o ícone de cada área são detalhe visual e moram do lado da UI
 * (veja `ui/components/AreaTarefaVisual.kt`).
 */
enum class AreaTarefa(val rotulo: String) {
    ACADEMICA("Acadêmica"),
    PROFISSIONAL("Profissional"),
    PESSOAL("Pessoal")
}
