package com.brunnakampferd.temvorax.data.calendario

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import java.time.LocalDate
import java.time.ZoneId

/**
 * Ponte com o Calendário NATIVO do Android (Content Provider) — de propósito,
 * não é a API do Google Calendar. Escrever aqui já basta: é o próprio Android
 * quem sincroniza esse calendário com a conta Google do usuário sozinho,
 * sem precisar pedir permissões OAuth extras pro app.
 *
 * Chama tudo daqui de dentro de uma coroutine (IO) — são operações de
 * ContentResolver, então bloqueantes.
 */
object CalendarioDispositivo {

    data class CalendarioDisponivel(val id: Long, val nomeExibicao: String, val nomeConta: String)

    /** Calendários do dispositivo em que dá pra escrever (nível OWNER ou CONTRIBUTOR). */
    fun calendariosGraviaveis(context: Context): List<CalendarioDisponivel> {
        val projecao = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.ACCOUNT_NAME
        )
        val selecao = "${CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL} >= ?"
        val argumentos = arrayOf(CalendarContract.Calendars.CAL_ACCESS_CONTRIBUTOR.toString())

        val resultado = mutableListOf<CalendarioDisponivel>()
        context.contentResolver.query(CalendarContract.Calendars.CONTENT_URI, projecao, selecao, argumentos, null)
            ?.use { cursor ->
                while (cursor.moveToNext()) {
                    resultado += CalendarioDisponivel(
                        id = cursor.getLong(0),
                        nomeExibicao = cursor.getString(1) ?: "Calendário",
                        nomeConta = cursor.getString(2) ?: ""
                    )
                }
            }
        return resultado
    }

    /**
     * Cria (ou atualiza, se [idExistente] ainda apontar pra um evento válido) o
     * evento de dia inteiro no calendário [calendarioId]. Devolve o `_ID` do
     * evento no dispositivo, pra guardar e reaproveitar nas próximas sincronizações
     * (assim "sincronizar de novo" atualiza em vez de duplicar).
     */
    fun sincronizarEvento(
        context: Context,
        calendarioId: Long,
        idExistente: Long?,
        titulo: String,
        data: LocalDate,
        observacoes: String
    ): Long {
        val fusoHorario = ZoneId.systemDefault()
        val inicioMillis = data.atStartOfDay(fusoHorario).toInstant().toEpochMilli()
        val fimMillis = data.plusDays(1).atStartOfDay(fusoHorario).toInstant().toEpochMilli()

        val valores = ContentValues().apply {
            put(CalendarContract.Events.CALENDAR_ID, calendarioId)
            put(CalendarContract.Events.TITLE, titulo)
            put(CalendarContract.Events.DESCRIPTION, observacoes)
            put(CalendarContract.Events.DTSTART, inicioMillis)
            put(CalendarContract.Events.DTEND, fimMillis)
            put(CalendarContract.Events.ALL_DAY, 1)
            put(CalendarContract.Events.EVENT_TIMEZONE, fusoHorario.id)
        }

        if (idExistente != null) {
            val uriExistente = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, idExistente)
            val linhasAtualizadas = context.contentResolver.update(uriExistente, valores, null, null)
            if (linhasAtualizadas > 0) return idExistente
            // Não achou (usuário apagou o evento direto no app de Calendário) — insere de novo abaixo.
        }

        val uriInserida = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, valores)
            ?: error("O calendário do dispositivo recusou o evento \"$titulo\".")
        return ContentUris.parseId(uriInserida)
    }
}
