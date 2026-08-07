package com.brunnakampferd.temvorax.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.brunnakampferd.temvorax.data.model.RegistroConclusao
import com.brunnakampferd.temvorax.data.model.TipoConclusao
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface RegistroConclusaoDao {
    /** Dias distintos com pelo menos uma conclusão — base de tudo que a [com.brunnakampferd.temvorax.domain.CalculadoraStreak] calcula. */
    @Query("SELECT DISTINCT concluidoEm FROM registros_conclusao ORDER BY concluidoEm DESC")
    fun observarDiasAtivos(): Flow<List<LocalDate>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(registro: RegistroConclusao)

    @Query("DELETE FROM registros_conclusao WHERE tipo = :tipo AND entidadeId = :entidadeId")
    suspend fun remover(tipo: TipoConclusao, entidadeId: Long)
}
