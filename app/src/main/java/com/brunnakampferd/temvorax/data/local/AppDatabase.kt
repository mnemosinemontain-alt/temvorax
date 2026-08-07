package com.brunnakampferd.temvorax.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.brunnakampferd.temvorax.data.local.dao.AnexoTccDao
import com.brunnakampferd.temvorax.data.local.dao.AtividadeProfissionalDao
import com.brunnakampferd.temvorax.data.local.dao.BolsaEditalDao
import com.brunnakampferd.temvorax.data.local.dao.CompromissoPessoalDao
import com.brunnakampferd.temvorax.data.local.dao.CompromissoProfissionalDao
import com.brunnakampferd.temvorax.data.local.dao.DisciplinaDao
import com.brunnakampferd.temvorax.data.local.dao.EstagioAcademicoDao
import com.brunnakampferd.temvorax.data.local.dao.EventoCalendarioAcademicoDao
import com.brunnakampferd.temvorax.data.local.dao.ExpedienteDao
import com.brunnakampferd.temvorax.data.local.dao.HoraComplementarDao
import com.brunnakampferd.temvorax.data.local.dao.PerfilDao
import com.brunnakampferd.temvorax.data.local.dao.ProvaDao
import com.brunnakampferd.temvorax.data.local.dao.RegistroConclusaoDao
import com.brunnakampferd.temvorax.data.local.dao.RegistroFrequenciaDao
import com.brunnakampferd.temvorax.data.local.dao.SemestreDao
import com.brunnakampferd.temvorax.data.local.dao.TarefaDao
import com.brunnakampferd.temvorax.data.local.dao.TarefaDiariaDao
import com.brunnakampferd.temvorax.data.local.dao.TccDao
import com.brunnakampferd.temvorax.data.local.dao.TrabalhoAcademicoDao
import com.brunnakampferd.temvorax.data.model.AnexoTcc
import com.brunnakampferd.temvorax.data.model.AtividadeProfissional
import com.brunnakampferd.temvorax.data.model.BolsaEdital
import com.brunnakampferd.temvorax.data.model.CompromissoPessoal
import com.brunnakampferd.temvorax.data.model.CompromissoProfissional
import com.brunnakampferd.temvorax.data.model.Disciplina
import com.brunnakampferd.temvorax.data.model.EstagioAcademico
import com.brunnakampferd.temvorax.data.model.EventoCalendarioAcademico
import com.brunnakampferd.temvorax.data.model.Expediente
import com.brunnakampferd.temvorax.data.model.HoraComplementar
import com.brunnakampferd.temvorax.data.model.Perfil
import com.brunnakampferd.temvorax.data.model.Prova
import com.brunnakampferd.temvorax.data.model.RegistroConclusao
import com.brunnakampferd.temvorax.data.model.RegistroFrequencia
import com.brunnakampferd.temvorax.data.model.Semestre
import com.brunnakampferd.temvorax.data.model.TarefaDia
import com.brunnakampferd.temvorax.data.model.TarefaDiaria
import com.brunnakampferd.temvorax.data.model.Tcc
import com.brunnakampferd.temvorax.data.model.TrabalhoAcademico

/**
 * Banco de dados local (Room) do Temvorax — tudo que o usuário cadastra
 * (tarefas rápidas, semestres, disciplinas, provas, compromissos, perfil...)
 * fica salvo aqui, no aparelho, e sobrevive a fechar o app.
 *
 * `fallbackToDestructiveMigration` porque o app ainda está em desenvolvimento
 * (sem usuários em produção ainda) — se o schema mudar, o banco é recriado do
 * zero em vez de escrevermos migrações manuais por enquanto. Antes de lançar
 * de verdade, isso precisa virar migrações reais pra não apagar dados de usuário.
 */
@Database(
    entities = [
        TarefaDia::class, Semestre::class, Disciplina::class, Prova::class,
        TrabalhoAcademico::class, RegistroFrequencia::class, EventoCalendarioAcademico::class,
        BolsaEdital::class, HoraComplementar::class, EstagioAcademico::class, Tcc::class,
        AnexoTcc::class, Expediente::class, CompromissoProfissional::class, AtividadeProfissional::class,
        TarefaDiaria::class, CompromissoPessoal::class, Perfil::class, RegistroConclusao::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tarefaDao(): TarefaDao
    abstract fun semestreDao(): SemestreDao
    abstract fun disciplinaDao(): DisciplinaDao
    abstract fun provaDao(): ProvaDao
    abstract fun trabalhoAcademicoDao(): TrabalhoAcademicoDao
    abstract fun registroFrequenciaDao(): RegistroFrequenciaDao
    abstract fun eventoCalendarioAcademicoDao(): EventoCalendarioAcademicoDao
    abstract fun bolsaEditalDao(): BolsaEditalDao
    abstract fun horaComplementarDao(): HoraComplementarDao
    abstract fun estagioAcademicoDao(): EstagioAcademicoDao
    abstract fun tccDao(): TccDao
    abstract fun anexoTccDao(): AnexoTccDao
    abstract fun expedienteDao(): ExpedienteDao
    abstract fun compromissoProfissionalDao(): CompromissoProfissionalDao
    abstract fun atividadeProfissionalDao(): AtividadeProfissionalDao
    abstract fun tarefaDiariaDao(): TarefaDiariaDao
    abstract fun compromissoPessoalDao(): CompromissoPessoalDao
    abstract fun perfilDao(): PerfilDao
    abstract fun registroConclusaoDao(): RegistroConclusaoDao

    companion object {
        @Volatile private var instancia: AppDatabase? = null

        fun obter(context: Context): AppDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "temvorax.db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { instancia = it }
            }
    }
}
