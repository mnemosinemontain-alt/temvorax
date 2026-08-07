package com.brunnakampferd.temvorax.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

/** Entidades da área Pessoal. */

@Entity(tableName = "tarefas_diarias")
data class TarefaDiaria(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titulo: String,
    val data: LocalDate,
    val concluida: Boolean = false
)

/**
 * @param diasSemana quando preenchido, o compromisso é RECORRENTE (ex.: Terapia
 *   toda terça às 15h) — nesse caso [data] vale como a data em que foi
 *   cadastrado/âncora, não uma ocorrência específica. Vazio/null = compromisso
 *   avulso de uma vez só, na [data] exata (comportamento de sempre).
 * @param recorrenteAte data em que a recorrência para de valer; null = sem
 *   data de fim definida (continua indefinidamente).
 */
@Entity(tableName = "compromissos_pessoais")
data class CompromissoPessoal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titulo: String,
    val data: LocalDate,
    val horaInicio: LocalTime? = null,
    val horaFim: LocalTime? = null,
    val local: String = "",
    val observacoes: String = "",
    val diasSemana: Set<DayOfWeek>? = null,
    val recorrenteAte: LocalDate? = null
)
