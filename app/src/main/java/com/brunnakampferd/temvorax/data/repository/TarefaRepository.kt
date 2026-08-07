package com.brunnakampferd.temvorax.data.repository

import com.brunnakampferd.temvorax.data.local.dao.TarefaDao
import com.brunnakampferd.temvorax.data.model.TarefaDia
import kotlinx.coroutines.flow.Flow

class TarefaRepository(private val dao: TarefaDao) {
    fun observarTodas(): Flow<List<TarefaDia>> = dao.observarTodas()

    suspend fun salvar(tarefa: TarefaDia) {
        if (tarefa.id == 0L) dao.inserir(tarefa) else dao.atualizar(tarefa)
    }

    suspend fun remover(tarefa: TarefaDia) = dao.remover(tarefa)
}
