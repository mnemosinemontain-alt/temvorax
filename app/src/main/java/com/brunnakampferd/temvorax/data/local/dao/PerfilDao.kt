package com.brunnakampferd.temvorax.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.brunnakampferd.temvorax.data.model.Perfil
import kotlinx.coroutines.flow.Flow

@Dao
interface PerfilDao {
    @Query("SELECT * FROM perfil WHERE id = 1")
    fun observar(): Flow<Perfil?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun salvar(perfil: Perfil)
}
