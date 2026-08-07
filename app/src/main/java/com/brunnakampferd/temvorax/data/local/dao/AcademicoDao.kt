package com.brunnakampferd.temvorax.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.brunnakampferd.temvorax.data.model.AnexoTcc
import com.brunnakampferd.temvorax.data.model.BolsaEdital
import com.brunnakampferd.temvorax.data.model.Disciplina
import com.brunnakampferd.temvorax.data.model.EstagioAcademico
import com.brunnakampferd.temvorax.data.model.EventoCalendarioAcademico
import com.brunnakampferd.temvorax.data.model.HoraComplementar
import com.brunnakampferd.temvorax.data.model.Prova
import com.brunnakampferd.temvorax.data.model.RegistroFrequencia
import com.brunnakampferd.temvorax.data.model.Semestre
import com.brunnakampferd.temvorax.data.model.Tcc
import com.brunnakampferd.temvorax.data.model.TrabalhoAcademico
import kotlinx.coroutines.flow.Flow

/** DAOs da área Acadêmica — todos juntos aqui pelo mesmo motivo dos models: mudam em conjunto. */

@Dao
interface SemestreDao {
    @Query("SELECT * FROM semestres ORDER BY nome DESC")
    fun observarTodos(): Flow<List<Semestre>>

    @Query("SELECT * FROM semestres WHERE id = :id")
    fun observarPorId(id: Long): Flow<Semestre?>

    @Insert
    suspend fun inserir(semestre: Semestre): Long

    @Update
    suspend fun atualizar(semestre: Semestre)

    @Delete
    suspend fun remover(semestre: Semestre)
}

@Dao
interface DisciplinaDao {
    @Query("SELECT * FROM disciplinas WHERE semestreId = :semestreId ORDER BY nome")
    fun observarPorSemestre(semestreId: Long): Flow<List<Disciplina>>

    /** Sem filtro de semestre — usada pra montar a agenda do dia na Home, que cruza todos os semestres ativos de uma vez. */
    @Query("SELECT * FROM disciplinas")
    fun observarTodas(): Flow<List<Disciplina>>

    @Insert
    suspend fun inserir(disciplina: Disciplina): Long

    @Update
    suspend fun atualizar(disciplina: Disciplina)

    @Delete
    suspend fun remover(disciplina: Disciplina)
}

@Dao
interface ProvaDao {
    @Query("SELECT * FROM provas WHERE semestreId = :semestreId ORDER BY data")
    fun observarPorSemestre(semestreId: Long): Flow<List<Prova>>

    @Query("SELECT * FROM provas")
    fun observarTodas(): Flow<List<Prova>>

    @Insert
    suspend fun inserir(prova: Prova): Long

    @Update
    suspend fun atualizar(prova: Prova)

    @Delete
    suspend fun remover(prova: Prova)
}

@Dao
interface TrabalhoAcademicoDao {
    @Query("SELECT * FROM trabalhos_academicos WHERE semestreId = :semestreId ORDER BY dataEntrega")
    fun observarPorSemestre(semestreId: Long): Flow<List<TrabalhoAcademico>>

    @Query("SELECT * FROM trabalhos_academicos")
    fun observarTodos(): Flow<List<TrabalhoAcademico>>

    @Insert
    suspend fun inserir(trabalho: TrabalhoAcademico): Long

    @Update
    suspend fun atualizar(trabalho: TrabalhoAcademico)

    @Delete
    suspend fun remover(trabalho: TrabalhoAcademico)
}

@Dao
interface RegistroFrequenciaDao {
    @Query("SELECT * FROM registros_frequencia WHERE disciplinaId = :disciplinaId ORDER BY data DESC")
    fun observarPorDisciplina(disciplinaId: Long): Flow<List<RegistroFrequencia>>

    @Insert
    suspend fun inserir(registro: RegistroFrequencia): Long

    @Update
    suspend fun atualizar(registro: RegistroFrequencia)

    @Delete
    suspend fun remover(registro: RegistroFrequencia)
}

@Dao
interface EventoCalendarioAcademicoDao {
    @Query("SELECT * FROM eventos_calendario_academico ORDER BY data")
    fun observarTodos(): Flow<List<EventoCalendarioAcademico>>

    @Insert
    suspend fun inserir(evento: EventoCalendarioAcademico): Long

    @Update
    suspend fun atualizar(evento: EventoCalendarioAcademico)

    @Delete
    suspend fun remover(evento: EventoCalendarioAcademico)
}

@Dao
interface BolsaEditalDao {
    @Query("SELECT * FROM bolsas_editais ORDER BY prazoFinal")
    fun observarTodas(): Flow<List<BolsaEdital>>

    @Insert
    suspend fun inserir(bolsa: BolsaEdital): Long

    @Update
    suspend fun atualizar(bolsa: BolsaEdital)

    @Delete
    suspend fun remover(bolsa: BolsaEdital)
}

@Dao
interface HoraComplementarDao {
    @Query("SELECT * FROM horas_complementares ORDER BY data DESC")
    fun observarTodas(): Flow<List<HoraComplementar>>

    @Insert
    suspend fun inserir(item: HoraComplementar): Long

    @Update
    suspend fun atualizar(item: HoraComplementar)

    @Delete
    suspend fun remover(item: HoraComplementar)
}

@Dao
interface EstagioAcademicoDao {
    @Query("SELECT * FROM estagios_academicos ORDER BY dataInicio DESC")
    fun observarTodos(): Flow<List<EstagioAcademico>>

    @Insert
    suspend fun inserir(estagio: EstagioAcademico): Long

    @Update
    suspend fun atualizar(estagio: EstagioAcademico)

    @Delete
    suspend fun remover(estagio: EstagioAcademico)
}

@Dao
interface TccDao {
    @Query("SELECT * FROM tccs ORDER BY dataPrevistaDefesa")
    fun observarTodos(): Flow<List<Tcc>>

    @Insert
    suspend fun inserir(tcc: Tcc): Long

    @Update
    suspend fun atualizar(tcc: Tcc)

    @Delete
    suspend fun remover(tcc: Tcc)
}

@Dao
interface AnexoTccDao {
    @Query("SELECT * FROM anexos_tcc WHERE tccId = :tccId ORDER BY adicionadoEm DESC")
    fun observarPorTcc(tccId: Long): Flow<List<AnexoTcc>>

    @Insert
    suspend fun inserir(anexo: AnexoTcc): Long

    @Delete
    suspend fun remover(anexo: AnexoTcc)
}
