package com.brunnakampferd.temvorax.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brunnakampferd.temvorax.data.model.Perfil
import com.brunnakampferd.temvorax.data.repository.PerfilRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Estado do envio da foto de perfil, separado do resto pra a tela poder mostrar um loading só nisso. */
sealed interface EnvioFotoEstado {
    data object Ocioso : EnvioFotoEstado
    data object Enviando : EnvioFotoEstado
    data class Erro(val mensagem: String) : EnvioFotoEstado
}

class PerfilViewModel(private val repository: PerfilRepository) : ViewModel() {

    val perfil: StateFlow<Perfil?> = repository.observar()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _envioFoto = MutableStateFlow<EnvioFotoEstado>(EnvioFotoEstado.Ocioso)
    val envioFoto: StateFlow<EnvioFotoEstado> = _envioFoto.asStateFlow()

    fun salvar(perfil: Perfil) {
        viewModelScope.launch { repository.salvar(perfil) }
    }

    /** Escolhe uma foto da galeria, sobe pro Firebase Storage e já salva a URL no perfil. */
    fun trocarFoto(uriEscolhida: Uri) {
        viewModelScope.launch {
            _envioFoto.value = EnvioFotoEstado.Enviando
            try {
                val url = repository.enviarFotoPerfil(uriEscolhida)
                val atual = perfil.value ?: Perfil()
                repository.salvar(atual.copy(fotoUrl = url))
                _envioFoto.value = EnvioFotoEstado.Ocioso
            } catch (e: Exception) {
                // Mensagem sempre amigável aqui — o texto técnico do Firebase (ex.:
                // "Object does not exist at location") não diz nada útil pro usuário.
                _envioFoto.value = EnvioFotoEstado.Erro(
                    "Não foi possível enviar a foto agora. Tenta de novo em instantes."
                )
            }
        }
    }
}
