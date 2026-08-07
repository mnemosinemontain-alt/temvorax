package com.brunnakampferd.temvorax.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.brunnakampferd.temvorax.data.model.CompromissoPessoal
import com.brunnakampferd.temvorax.data.model.TarefaDiaria
import kotlinx.coroutines.flow.Flow

@Dao
interface TarefaDiariaDao {
    @Query("SELECT * FROM tarefas_diarias ORDER BY data DESC")
    fun observarTodas(): Flow<List<TarefaDiaria>>

    @Insert
    suspend fun inserir(item: TarefaDiaria): Long

    @Update
    suspend fun atualizar(item: TarefaDiaria)

    @Delete
    suspend fun remover(item: TarefaDiaria)
}

@Dao
interface CompromissoPessoalDao {
    @Query("SELECT * FROM compromissos_pessoais ORDER BY data")
    fun observarTodos(): Flow<List<CompromissoPessoal>>

    @Insert
    suspend fun inserir(item: CompromissoPessoal): Long

    @Update
    suspend fun atualizar(item: CompromissoPessoal)

    @Delete
    suspend fun remover(item: CompromissoPessoal)
}
