package com.brunnakampferd.temvorax.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.brunnakampferd.temvorax.data.model.TarefaDia
import kotlinx.coroutines.flow.Flow

@Dao
interface TarefaDao {
    @Query("SELECT * FROM tarefas ORDER BY data DESC")
    fun observarTodas(): Flow<List<TarefaDia>>

    @Insert
    suspend fun inserir(tarefa: TarefaDia): Long

    @Update
    suspend fun atualizar(tarefa: TarefaDia)

    @Delete
    suspend fun remover(tarefa: TarefaDia)
}
