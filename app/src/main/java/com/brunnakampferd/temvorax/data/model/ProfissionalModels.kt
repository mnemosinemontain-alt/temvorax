package com.brunnakampferd.temvorax.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

/** Entidades da área Profissional. */

/** Horário de trabalho recorrente (ex.: seg-sex, 09h-18h) — um bloco semanal por registro. */
@Entity(tableName = "expedientes")
data class Expediente(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val diasSemana: Set<DayOfWeek> = emptySet(),
    val horaInicio: LocalTime,
    val horaFim: LocalTime,
    val local: String = "",
    val observacoes: String = ""
)

/**
 * @param diasSemana quando preenchido, o compromisso é RECORRENTE (ex.: toda
 *   terça às 15h) — nesse caso [data] vale como a data em que foi cadastrado/
 *   âncora, não uma ocorrência específica. Vazio/null = compromisso avulso de
 *   uma vez só, na [data] exata (comportamento de sempre).
 * @param recorrenteAte data em que a recorrência para de valer; null = sem
 *   data de fim definida (continua indefinidamente).
 */
@Entity(tableName = "compromissos_profissionais")
data class CompromissoProfissional(
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

@Entity(tableName = "atividades_profissionais")
data class AtividadeProfissional(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titulo: String,
    val descricao: String = "",
    val data: LocalDate,
    val concluida: Boolean = false
)
