package com.brunnakampferd.temvorax.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

/**
 * Entidades da área Acadêmica. Ficam todas juntas neste arquivo (em vez de
 * um arquivo por classe) porque são pequenas e sempre mudam em conjunto —
 * é mais fácil ver o desenho geral da área acadêmica inteira de uma vez.
 *
 * Semestre funciona como "pasta": Disciplina, Prova, TrabalhoAcademico e
 * RegistroFrequencia sempre pertencem a um semestre (via semestreId), e ficam
 * organizados dentro da tela de detalhe daquele semestre — não soltos.
 *
 * Calendário acadêmico, Bolsas/editais, Horas complementares, Estágio e TCC
 * são informativos/pessoais e não mudam com o semestre selecionado.
 */

@Entity(tableName = "semestres")
data class Semestre(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nome: String, // ex.: "2026.1"
    val dataInicio: LocalDate? = null,
    val dataFim: LocalDate? = null,
    val ativo: Boolean = true
)

@Entity(
    tableName = "disciplinas",
    foreignKeys = [ForeignKey(
        entity = Semestre::class,
        parentColumns = ["id"],
        childColumns = ["semestreId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("semestreId")]
)
data class Disciplina(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val semestreId: Long,
    val nome: String,
    val professor: String = "",
    // Horário semanal recorrente — mesmo bloco de horário se repete em cada
    // dia marcado (ex.: Seg e Qua, 14h-16h). Um bloco só por disciplina ainda
    // (não dá pra ter horários diferentes em dias diferentes da mesma matéria).
    val diasSemana: Set<DayOfWeek> = emptySet(),
    val horaInicio: LocalTime? = null,
    val horaFim: LocalTime? = null,
    // Período em que a disciplina vale DENTRO do semestre — null em qualquer um
    // dos dois significa "vale o semestre inteiro" (o comportamento de sempre).
    val dataInicioMateria: LocalDate? = null,
    val dataFimMateria: LocalDate? = null
)

@Entity(
    tableName = "provas",
    foreignKeys = [
        ForeignKey(entity = Semestre::class, parentColumns = ["id"], childColumns = ["semestreId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Disciplina::class, parentColumns = ["id"], childColumns = ["disciplinaId"], onDelete = ForeignKey.SET_NULL)
    ],
    indices = [Index("semestreId"), Index("disciplinaId")]
)
data class Prova(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val semestreId: Long,
    val disciplinaId: Long? = null,
    val titulo: String,
    val data: LocalDate,
    val observacoes: String = ""
)

@Entity(
    tableName = "trabalhos_academicos",
    foreignKeys = [
        ForeignKey(entity = Semestre::class, parentColumns = ["id"], childColumns = ["semestreId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Disciplina::class, parentColumns = ["id"], childColumns = ["disciplinaId"], onDelete = ForeignKey.SET_NULL)
    ],
    indices = [Index("semestreId"), Index("disciplinaId")]
)
data class TrabalhoAcademico(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val semestreId: Long,
    val disciplinaId: Long? = null,
    val titulo: String,
    val dataEntrega: LocalDate,
    val concluido: Boolean = false,
    val observacoes: String = ""
)

@Entity(
    tableName = "registros_frequencia",
    foreignKeys = [ForeignKey(
        entity = Disciplina::class,
        parentColumns = ["id"],
        childColumns = ["disciplinaId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("disciplinaId")]
)
data class RegistroFrequencia(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val disciplinaId: Long,
    val data: LocalDate,
    val presente: Boolean = true
)

/** Datas oficiais da instituição (início/fim de período, feriados, semana de provas...) — fixo, não muda com o semestre escolhido. */
@Entity(tableName = "eventos_calendario_academico")
data class EventoCalendarioAcademico(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titulo: String,
    val data: LocalDate,
    val observacoes: String = "",
    /** _ID do evento correspondente no Calendário nativo do Android, depois de sincronizado — null até a 1ª sincronização. */
    val idEventoDispositivo: Long? = null
)

enum class StatusBolsa(val rotulo: String) {
    NAO_INSCRITO("Não inscrito"),
    INSCRITO("Inscrito"),
    APROVADO("Aprovado"),
    REPROVADO("Reprovado")
}

/** Distingue Bolsas de Editais dentro da mesma sub-área — cada um com sua própria aba na lista. */
enum class TipoBolsaEdital(val rotulo: String, val rotuloPlural: String) {
    BOLSA("Bolsa", "Bolsas"),
    EDITAL("Edital", "Editais")
}

@Entity(tableName = "bolsas_editais")
data class BolsaEdital(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nome: String,
    val tipo: TipoBolsaEdital = TipoBolsaEdital.BOLSA,
    val instituicao: String = "",
    val dataAberturaInscricoes: LocalDate? = null,
    val prazoFinal: LocalDate? = null,
    val valor: String = "",
    val status: StatusBolsa = StatusBolsa.NAO_INSCRITO,
    val observacoes: String = ""
)

@Entity(tableName = "horas_complementares")
data class HoraComplementar(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val descricao: String,
    val categoria: String = "",
    val horas: Double,
    val data: LocalDate,
    val observacoes: String = ""
)

@Entity(tableName = "estagios_academicos")
data class EstagioAcademico(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val empresa: String,
    val cargo: String = "",
    val supervisor: String = "",
    val dataInicio: LocalDate? = null,
    val dataTerminoPrevista: LocalDate? = null,
    val cargaHorariaSemanal: Double = 0.0,
    val totalHorasCumpridas: Double = 0.0,
    val observacoes: String = ""
)

enum class EtapaTcc(val rotulo: String) {
    TEMA_DEFINIDO("Tema definido"),
    PROJETO_APROVADO("Projeto aprovado"),
    ESCREVENDO("Escrevendo"),
    REVISAO("Revisão"),
    DEFENDIDO("Defendido")
}

@Entity(tableName = "tccs")
data class Tcc(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titulo: String,
    val orientador: String = "",
    val dataInicio: LocalDate? = null,
    val dataPrevistaDefesa: LocalDate? = null,
    val etapaAtual: EtapaTcc = EtapaTcc.TEMA_DEFINIDO,
    val observacoes: String = ""
)

/**
 * Um PDF anexado a um TCC (pesquisa, referência, versão do próprio texto...).
 * O arquivo em si vai pro Firebase Storage — aqui fica só a referência
 * (nome pra exibir + URL de download), do mesmo jeito que Perfil.fotoUrl.
 */
@Entity(
    tableName = "anexos_tcc",
    foreignKeys = [ForeignKey(entity = Tcc::class, parentColumns = ["id"], childColumns = ["tccId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("tccId")]
)
data class AnexoTcc(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tccId: Long,
    val nomeArquivo: String,
    val url: String,
    val adicionadoEm: LocalDate = LocalDate.now()
)
