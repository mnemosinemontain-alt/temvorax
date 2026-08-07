package com.brunnakampferd.temvorax.data.local

import androidx.room.TypeConverter
import com.brunnakampferd.temvorax.data.model.AreaTarefa
import com.brunnakampferd.temvorax.data.model.EtapaTcc
import com.brunnakampferd.temvorax.data.model.NivelEnsino
import com.brunnakampferd.temvorax.data.model.StatusBolsa
import com.brunnakampferd.temvorax.data.model.TipoBolsaEdital
import com.brunnakampferd.temvorax.data.model.TipoConclusao
import com.brunnakampferd.temvorax.data.model.Turno
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

/**
 * Conversores pro Room saber gravar/ler tipos que ele não entende nativamente:
 * datas/horas do java.time e nossos enums de domínio. O Room só chama esses
 * métodos quando o valor não é nulo — campos opcionais (ex.: data de
 * nascimento vazia) passam null direto, sem precisar tratar isso aqui.
 */
class Converters {
    @TypeConverter
    fun fromLocalDate(data: LocalDate?): String? = data?.toString()

    @TypeConverter
    fun toLocalDate(valor: String?): LocalDate? = valor?.let { LocalDate.parse(it) }

    @TypeConverter
    fun fromLocalTime(hora: LocalTime?): String? = hora?.toString()

    @TypeConverter
    fun toLocalTime(valor: String?): LocalTime? = valor?.let { LocalTime.parse(it) }

    @TypeConverter
    fun fromDayOfWeek(dia: DayOfWeek?): Int? = dia?.value

    @TypeConverter
    fun toDayOfWeek(valor: Int?): DayOfWeek? = valor?.let { DayOfWeek.of(it) }

    @TypeConverter
    fun fromAreaTarefa(area: AreaTarefa?): String? = area?.name

    @TypeConverter
    fun toAreaTarefa(valor: String?): AreaTarefa? = valor?.let { AreaTarefa.valueOf(it) }

    @TypeConverter
    fun fromNivelEnsino(nivel: NivelEnsino?): String? = nivel?.name

    @TypeConverter
    fun toNivelEnsino(valor: String?): NivelEnsino? = valor?.let { NivelEnsino.valueOf(it) }

    @TypeConverter
    fun fromTurno(turno: Turno?): String? = turno?.name

    @TypeConverter
    fun toTurno(valor: String?): Turno? = valor?.let { Turno.valueOf(it) }

    @TypeConverter
    fun fromStatusBolsa(status: StatusBolsa?): String? = status?.name

    @TypeConverter
    fun toStatusBolsa(valor: String?): StatusBolsa? = valor?.let { StatusBolsa.valueOf(it) }

    @TypeConverter
    fun fromEtapaTcc(etapa: EtapaTcc?): String? = etapa?.name

    @TypeConverter
    fun toEtapaTcc(valor: String?): EtapaTcc? = valor?.let { EtapaTcc.valueOf(it) }

    @TypeConverter
    fun fromTipoBolsaEdital(tipo: TipoBolsaEdital?): String? = tipo?.name

    @TypeConverter
    fun toTipoBolsaEdital(valor: String?): TipoBolsaEdital? = valor?.let { TipoBolsaEdital.valueOf(it) }

    @TypeConverter
    fun fromTipoConclusao(tipo: TipoConclusao?): String? = tipo?.name

    @TypeConverter
    fun toTipoConclusao(valor: String?): TipoConclusao? = valor?.let { TipoConclusao.valueOf(it) }

    /**
     * Conjunto de dias da semana (ex.: "toda segunda e quarta") — usado em
     * Disciplina/Expediente/Compromissos recorrentes. Serializa como CSV dos
     * nomes do enum ("MONDAY,WEDNESDAY"); vale tanto pra campo obrigatório
     * (Set vazio quando não houver dia nenhum) quanto opcional (null = não é
     * recorrente), do mesmo jeito que fromDayOfWeek/toDayOfWeek já cobrem os
     * dois casos pro DayOfWeek único.
     */
    @TypeConverter
    fun fromDiasSemana(dias: Set<DayOfWeek>?): String? = dias?.joinToString(",") { it.name }

    @TypeConverter
    fun toDiasSemana(valor: String?): Set<DayOfWeek>? =
        valor?.split(",")?.filter { it.isNotBlank() }?.map { DayOfWeek.valueOf(it) }?.toSet()
}
