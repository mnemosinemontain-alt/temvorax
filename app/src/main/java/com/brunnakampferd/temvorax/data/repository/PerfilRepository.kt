package com.brunnakampferd.temvorax.data.repository

import android.content.Context
import android.net.Uri
import com.brunnakampferd.temvorax.data.cloudinary.ClienteCloudinary
import com.brunnakampferd.temvorax.data.local.dao.PerfilDao
import com.brunnakampferd.temvorax.data.model.Perfil
import kotlinx.coroutines.flow.Flow

class PerfilRepository(
    private val dao: PerfilDao,
    private val context: Context
) {
    fun observar(): Flow<Perfil?> = dao.observar()

    suspend fun salvar(perfil: Perfil) = dao.salvar(perfil)

    /**
     * Sobe a foto escolhida pro Cloudinary e devolve a URL pública, pra salvar
     * em Perfil.fotoUrl. Lança exceção se o upload falhar (ver ClienteCloudinary).
     */
    suspend fun enviarFotoPerfil(uriLocal: Uri): String =
        ClienteCloudinary.enviarImagem(context, uriLocal, "foto_perfil.jpg")
}
