package com.brunnakampferd.temvorax.data.repository

import com.brunnakampferd.temvorax.data.local.dao.AtividadeProfissionalDao
import com.brunnakampferd.temvorax.data.local.dao.CompromissoProfissionalDao
import com.brunnakampferd.temvorax.data.local.dao.ExpedienteDao
import com.brunnakampferd.temvorax.data.model.AtividadeProfissional
import com.brunnakampferd.temvorax.data.model.CompromissoProfissional
import com.brunnakampferd.temvorax.data.model.Expediente
import com.brunnakampferd.temvorax.domain.ItemAgenda
import com.brunnakampferd.temvorax.domain.itensProfissionaisDoDia
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate

class ProfissionalRepository(
    private val expedienteDao: ExpedienteDao,
    private val compromissoDao: CompromissoProfissionalDao,
    private val atividadeDao: AtividadeProfissionalDao
) {
    /** Agenda do dia da área Profissional (Expediente + Compromissos + Atividades de hoje) — pra aba "Hoje" da Home. */
    fun observarItensProfissionaisDoDia(hoje: LocalDate): Flow<List<ItemAgenda>> =
        combine(observarExpedientes(), observarCompromissos(), observarAtividades()) { expedientes, compromissos, atividades ->
            itensProfissionaisDoDia(hoje, expedientes, compromissos, atividades)
        }

    // ---- Expediente ----
    fun observarExpedientes(): Flow<List<Expediente>> = expedienteDao.observarTodos()
    suspend fun salvarExpediente(item: Expediente) {
        if (item.id == 0L) expedienteDao.inserir(item) else expedienteDao.atualizar(item)
    }
    suspend fun removerExpediente(item: Expediente) = expedienteDao.remover(item)

    // ---- Compromissos profissionais ----
    fun observarCompromissos(): Flow<List<CompromissoProfissional>> = compromissoDao.observarTodos()
    suspend fun salvarCompromisso(item: CompromissoProfissional) {
        if (item.id == 0L) compromissoDao.inserir(item) else compromissoDao.atualizar(item)
    }
    suspend fun removerCompromisso(item: CompromissoProfissional) = compromissoDao.remover(item)

    // ---- Atividades profissionais ----
    fun observarAtividades(): Flow<List<AtividadeProfissional>> = atividadeDao.observarTodas()
    suspend fun salvarAtividade(item: AtividadeProfissional) {
        if (item.id == 0L) atividadeDao.inserir(item) else atividadeDao.atualizar(item)
    }
    suspend fun removerAtividade(item: AtividadeProfissional) = atividadeDao.remover(item)
}
