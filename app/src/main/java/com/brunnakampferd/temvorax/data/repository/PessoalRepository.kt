package com.brunnakampferd.temvorax.data.repository

import com.brunnakampferd.temvorax.data.local.dao.CompromissoPessoalDao
import com.brunnakampferd.temvorax.data.local.dao.TarefaDiariaDao
import com.brunnakampferd.temvorax.data.model.CompromissoPessoal
import com.brunnakampferd.temvorax.data.model.TarefaDiaria
import com.brunnakampferd.temvorax.domain.ItemAgenda
import com.brunnakampferd.temvorax.domain.itensPessoaisDoDia
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate

class PessoalRepository(
    private val tarefaDiariaDao: TarefaDiariaDao,
    private val compromissoDao: CompromissoPessoalDao
) {
    /** Agenda do dia da área Pessoal (Tarefas diárias + Compromissos de hoje) — pra aba "Hoje" da Home. */
    fun observarItensPessoaisDoDia(hoje: LocalDate): Flow<List<ItemAgenda>> =
        combine(observarTarefasDiarias(), observarCompromissos()) { tarefasDiarias, compromissos ->
            itensPessoaisDoDia(hoje, tarefasDiarias, compromissos)
        }

    // ---- Tarefas diárias ----
    fun observarTarefasDiarias(): Flow<List<TarefaDiaria>> = tarefaDiariaDao.observarTodas()
    suspend fun salvarTarefaDiaria(item: TarefaDiaria) {
        if (item.id == 0L) tarefaDiariaDao.inserir(item) else tarefaDiariaDao.atualizar(item)
    }
    suspend fun removerTarefaDiaria(item: TarefaDiaria) = tarefaDiariaDao.remover(item)

    // ---- Compromissos pessoais ----
    fun observarCompromissos(): Flow<List<CompromissoPessoal>> = compromissoDao.observarTodos()
    suspend fun salvarCompromisso(item: CompromissoPessoal) {
        if (item.id == 0L) compromissoDao.inserir(item) else compromissoDao.atualizar(item)
    }
    suspend fun removerCompromisso(item: CompromissoPessoal) = compromissoDao.remover(item)
}
