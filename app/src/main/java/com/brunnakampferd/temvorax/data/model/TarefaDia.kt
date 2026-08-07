package com.brunnakampferd.temvorax.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Representa uma tarefa/compromisso rápido do usuário, mostrado nas abas
 * Hoje/Pendências/Concluído. É a lista de captura rápida, tageada por área —
 * diferente das entidades mais específicas de cada área (Prova, Semestre,
 * Expediente etc.), que moram nos "hubs" de cada área.
 *
 * @param data a data em que a tarefa está marcada. É o que decide se ela
 *   aparece na aba "Hoje" (data == hoje) — a aba "Pendências" ignora esse
 *   campo de propósito e mostra tudo que não está concluído, não importa a data.
 */
@Entity(tableName = "tarefas")
data class TarefaDia(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titulo: String,
    val area: AreaTarefa,
    val data: LocalDate,
    val concluida: Boolean = false
)
