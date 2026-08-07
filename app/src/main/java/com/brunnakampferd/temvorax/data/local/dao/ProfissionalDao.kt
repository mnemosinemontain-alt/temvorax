package com.brunnakampferd.temvorax.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.brunnakampferd.temvorax.data.model.AtividadeProfissional
import com.brunnakampferd.temvorax.data.model.CompromissoProfissional
import com.brunnakampferd.temvorax.data.model.Expediente
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpedienteDao {
    // Sem ORDER BY aqui: diasSemana virou um conjunto (serializado como texto),
    // então ordenar por dia da semana precisa ser feito em Kotlin, não em SQL
    // (ver ExpedienteScreen.kt).
    @Query("SELECT * FROM expedientes")
    fun observarTodos(): Flow<List<Expediente>>

    @Insert
    suspend fun inserir(item: Expediente): Long

    @Update
    suspend fun atualizar(item: Expediente)

    @Delete
    suspend fun remover(item: Expediente)
}

@Dao
interface CompromissoProfissionalDao {
    @Query("SELECT * FROM compromissos_profissionais ORDER BY data")
    fun observarTodos(): Flow<List<CompromissoProfissional>>

    @Insert
    suspend fun inserir(item: CompromissoProfissional): Long

    @Update
    suspend fun atualizar(item: CompromissoProfissional)

    @Delete
    suspend fun remover(item: CompromissoProfissional)
}

@Dao
interface AtividadeProfissionalDao {
    @Query("SELECT * FROM atividades_profissionais ORDER BY data DESC")
    fun observarTodas(): Flow<List<AtividadeProfissional>>

    @Insert
    suspend fun inserir(item: AtividadeProfissional): Long

    @Update
    suspend fun atualizar(item: AtividadeProfissional)

    @Delete
    suspend fun remover(item: AtividadeProfissional)
}
