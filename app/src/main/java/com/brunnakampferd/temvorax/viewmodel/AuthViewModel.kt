package com.brunnakampferd.temvorax.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brunnakampferd.temvorax.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Representa tudo que a tela de Auth precisa saber sobre o estado atual.
 *
 * Eu uso uma "sealed interface" aqui (em vez de vários Booleans soltos tipo
 * isLoading + hasError + errorMessage) porque assim é IMPOSSÍVEL a tela cair
 * num estado inconsistente, tipo "carregando E com erro ao mesmo tempo".
 * Só existe um estado por vez, e o Kotlin me obriga a tratar todos eles.
 */
sealed interface AuthUiState {
    data object Ocioso : AuthUiState
    data object Carregando : AuthUiState
    data object Sucesso : AuthUiState
    data class Erro(val mensagem: String) : AuthUiState
}

/**
 * Estado do fluxo de "esqueci minha senha", separado do [AuthUiState] principal
 * de propósito — ele roda dentro de um diálogo por cima da tela de login, e não
 * deve interferir (nem ser resetado por) o estado de entrar/criar conta.
 */
sealed interface RecuperacaoSenhaEstado {
    data object Ocioso : RecuperacaoSenhaEstado
    data object Enviando : RecuperacaoSenhaEstado
    data object Enviado : RecuperacaoSenhaEstado
    data class Erro(val mensagem: String) : RecuperacaoSenhaEstado
}

/**
 * AuthViewModel.
 *
 * Guarda o estado da tela de autenticação e expõe funções que a UI chama
 * (entrarComEmail, criarConta, entrarComGoogle). A tela (Composable) NUNCA
 * fala direto com o Firebase — ela só observa o "uiState" e chama essas funções.
 *
 * Isso separa "o que a tela mostra" de "como a autenticação funciona por dentro",
 * o que facilita muito testar e trocar coisas no futuro.
 */
class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Ocioso)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _recuperacaoSenha = MutableStateFlow<RecuperacaoSenhaEstado>(RecuperacaoSenhaEstado.Ocioso)
    val recuperacaoSenha: StateFlow<RecuperacaoSenhaEstado> = _recuperacaoSenha.asStateFlow()

    /** Chamado quando o usuário está no modo "Entrar" e clica no botão principal. */
    fun entrarComEmail(email: String, senha: String) {
        if (!validarCampos(email, senha)) return

        _uiState.value = AuthUiState.Carregando
        viewModelScope.launch {
            try {
                repository.entrarComEmail(email, senha)
                _uiState.value = AuthUiState.Sucesso
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Erro(traduzirErro(e))
            }
        }
    }

    /** Chamado quando o usuário está no modo "Criar conta" e clica no botão principal. */
    fun criarConta(email: String, senha: String) {
        if (!validarCampos(email, senha)) return

        _uiState.value = AuthUiState.Carregando
        viewModelScope.launch {
            try {
                repository.criarContaComEmail(email, senha)
                _uiState.value = AuthUiState.Sucesso
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Erro(traduzirErro(e))
            }
        }
    }

    /**
     * Chamado depois que a Activity conseguiu o idToken do Google Sign-In.
     * (A obtenção do idToken em si não é responsabilidade do ViewModel — veja
     * o comentário no AuthRepository sobre isso.)
     */
    fun entrarComGoogle(idToken: String) {
        _uiState.value = AuthUiState.Carregando
        viewModelScope.launch {
            try {
                repository.entrarComGoogle(idToken)
                _uiState.value = AuthUiState.Sucesso
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Erro(traduzirErro(e))
            }
        }
    }

    /** Validação simples no cliente, antes de gastar uma chamada de rede à toa. */
    private fun validarCampos(email: String, senha: String): Boolean {
        if (email.isBlank() || !email.contains("@")) {
            _uiState.value = AuthUiState.Erro("Digite um e-mail válido.")
            return false
        }
        if (senha.length < 6) {
            _uiState.value = AuthUiState.Erro("A senha precisa ter pelo menos 6 caracteres.")
            return false
        }
        return true
    }

    /**
     * O Firebase manda mensagens de erro em inglês e meio técnicas.
     * Aqui eu traduzo as mais comuns pra algo que o usuário entenda.
     */
    private fun traduzirErro(e: Exception): String {
        val msg = e.message ?: return "Algo deu errado. Tenta de novo em instantes."
        return when {
            msg.contains("email address is already in use", ignoreCase = true) ->
                "Esse e-mail já está cadastrado. Tenta entrar em vez de criar conta."
            msg.contains("password is invalid", ignoreCase = true) ||
                    msg.contains("no user record", ignoreCase = true) ->
                "E-mail ou senha incorretos."
            msg.contains("badly formatted", ignoreCase = true) ->
                "Esse e-mail não parece válido."
            msg.contains("network error", ignoreCase = true) ->
                "Sem conexão com a internet. Verifica sua rede e tenta de novo."
            else -> "Não foi possível continuar. Tenta novamente em instantes."
        }
    }

    /** Reseta o estado pra Ocioso — útil ao trocar entre os modos Login/Cadastro. */
    fun limparEstado() {
        _uiState.value = AuthUiState.Ocioso
    }

    /** Chamado ao confirmar o e-mail no diálogo de "esqueci minha senha". */
    fun recuperarSenha(email: String) {
        if (email.isBlank() || !email.contains("@")) {
            _recuperacaoSenha.value = RecuperacaoSenhaEstado.Erro("Digite um e-mail válido.")
            return
        }

        _recuperacaoSenha.value = RecuperacaoSenhaEstado.Enviando
        viewModelScope.launch {
            try {
                repository.enviarEmailRecuperacaoSenha(email)
                _recuperacaoSenha.value = RecuperacaoSenhaEstado.Enviado
            } catch (e: Exception) {
                _recuperacaoSenha.value = RecuperacaoSenhaEstado.Erro(traduzirErroRecuperacao(e))
            }
        }
    }

    /** Reseta o estado do diálogo de recuperação — chamado ao fechá-lo, pra reabrir sempre do zero. */
    fun limparRecuperacaoSenha() {
        _recuperacaoSenha.value = RecuperacaoSenhaEstado.Ocioso
    }

    /** Mesma ideia de [traduzirErro], mas com mensagens que fazem sentido no contexto de recuperação de senha. */
    private fun traduzirErroRecuperacao(e: Exception): String {
        val msg = e.message ?: return "Algo deu errado. Tenta de novo em instantes."
        return when {
            msg.contains("no user record", ignoreCase = true) ->
                "Não existe conta cadastrada com esse e-mail."
            msg.contains("badly formatted", ignoreCase = true) ->
                "Esse e-mail não parece válido."
            msg.contains("network error", ignoreCase = true) ->
                "Sem conexão com a internet. Verifica sua rede e tenta de novo."
            else -> "Não foi possível enviar o e-mail agora. Tenta novamente em instantes."
        }
    }

    /** Desloga o usuário e volta o estado pra Ocioso, pronto pra um próximo login. */
    fun sair() {
        repository.sair()
        _uiState.value = AuthUiState.Ocioso
    }

    /** Nome do usuário logado, pra exibir na Home. */
    fun nomeUsuarioAtual(): String = repository.nomeUsuarioAtual()

    /**
     * true se já existe uma sessão válida (o Firebase Auth mantém login salvo
     * entre aberturas do app por padrão) — usado ao abrir o app pra pular
     * Welcome/Login e ir direto pra Home.
     */
    fun usuarioJaLogado(): Boolean = repository.usuarioJaLogado()
}