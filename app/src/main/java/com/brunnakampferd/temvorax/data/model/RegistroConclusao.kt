package com.brunnakampferd.temvorax.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * De qual das 4 tabelas "concluíveis" do app veio uma conclusão registrada
 * aqui — cada uma continua com seu próprio boolean de conclusão (não muda),
 * isso aqui é só um LOG paralelo de "quando" aconteceu.
 */
enum class TipoConclusao {
    TAREFA_DIA,
    TAREFA_DIARIA,
    TRABALHO_ACADEMICO,
    ATIVIDADE_PROFISSIONAL
}

/**
 * Log único de conclusões, usado pra calcular streak/consistência sem
 * precisar cruzar as 4 tabelas de tarefa/atividade toda vez. Uma linha por
 * item concluído — desmarcar remove a linha (não deixa histórico "cancelado"),
 * pra um toque errado não distorcer o streak.
 *
 * Índice único em (tipo, entidadeId) evita duplicata se o mesmo item for
 * marcado/desmarcado/marcado de novo.
 */
@Entity(
    tableName = "registros_conclusao",
    indices = [Index(value = ["tipo", "entidadeId"], unique = true)]
)
data class RegistroConclusao(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tipo: TipoConclusao,
    val entidadeId: Long,
    val area: AreaTarefa,
    val concluidoEm: LocalDate
)
