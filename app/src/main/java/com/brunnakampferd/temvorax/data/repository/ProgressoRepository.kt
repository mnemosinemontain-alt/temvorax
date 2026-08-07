package com.brunnakampferd.temvorax.data.repository

import com.brunnakampferd.temvorax.data.local.dao.RegistroConclusaoDao
import com.brunnakampferd.temvorax.data.model.AreaTarefa
import com.brunnakampferd.temvorax.data.model.RegistroConclusao
import com.brunnakampferd.temvorax.data.model.TipoConclusao
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Repository do log de conclusões (streak/consistência) — fica separado dos
 * repositories de Tarefa/Pessoal/Acadêmica/Profissional de propósito, já que
 * é um registro TRANSVERSAL às 4 áreas, não pertence a nenhuma delas.
 */
class ProgressoRepository(private val dao: RegistroConclusaoDao) {

    fun observarDiasAtivos(): Flow<List<LocalDate>> = dao.observarDiasAtivos()

    suspend fun registrarConclusao(tipo: TipoConclusao, entidadeId: Long, area: AreaTarefa, data: LocalDate = LocalDate.now()) {
        dao.inserir(RegistroConclusao(tipo = tipo, entidadeId = entidadeId, area = area, concluidoEm = data))
    }

    suspend fun desfazerConclusao(tipo: TipoConclusao, entidadeId: Long) {
        dao.remover(tipo, entidadeId)
    }
}
