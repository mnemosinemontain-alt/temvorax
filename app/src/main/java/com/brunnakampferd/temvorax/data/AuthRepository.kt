package com.brunnakampferd.temvorax.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

/**
 * AuthRepository.
 *
 * Essa classe é a ÚNICA parte do app que conversa diretamente com o FirebaseAuth.
 * Eu separei assim de propósito: se um dia a gente trocar de provedor de auth
 * (ou adicionar login por Facebook, Apple, etc.), só mexo aqui — o ViewModel e a
 * tela nem ficam sabendo que "por baixo" mudou alguma coisa.
 *
 * Uso "suspend fun" + "await()" em vez de callbacks, porque assim consigo chamar
 * essas funções dentro de uma coroutine no ViewModel de um jeito bem mais limpo
 * (sem "callback hell").
 */
class AuthRepository(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    /** Cria uma conta nova com e-mail e senha. Lança exceção se der erro (e-mail já existe, senha fraca, etc). */
    suspend fun criarContaComEmail(email: String, senha: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, senha).await()
    }

    /** Faz login com e-mail e senha já cadastrados. Lança exceção se credenciais estiverem erradas. */
    suspend fun entrarComEmail(email: String, senha: String) {
        firebaseAuth.signInWithEmailAndPassword(email, senha).await()
    }

    /**
     * Faz login (ou cria conta automaticamente, se for a primeira vez) usando o
     * idToken que a gente recebe depois do usuário escolher a conta Google dele.
     *
     * O idToken em si é obtido fora daqui (na Activity, via GoogleSignInClient),
     * porque isso depende de Context/Activity — coisa que o Repository não deveria
     * conhecer, pra manter a separação de responsabilidades.
     */
    suspend fun entrarComGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential).await()
    }

    /**
     * Manda um e-mail de redefinição de senha pro endereço informado. Lança
     * exceção se o e-mail estiver mal formatado ou (dependendo da config do
     * projeto no Firebase) se não houver conta cadastrada com ele.
     */
    suspend fun enviarEmailRecuperacaoSenha(email: String) {
        firebaseAuth.sendPasswordResetEmail(email).await()
    }

    /** Desloga o usuário atual. */
    fun sair() {
        firebaseAuth.signOut()
    }

    /** Retorna true se já existe alguém logado (útil pra pular a tela de login ao abrir o app). */
    fun usuarioJaLogado(): Boolean {
        return firebaseAuth.currentUser != null
    }

    /**
     * Nome pra exibir na Home ("Olá, Fulano!"). Tenta o nome de exibição
     * (vem preenchido automaticamente no login com Google); se não tiver
     * (ex.: cadastro por e-mail/senha, que não pede nome), cai pro início
     * do e-mail; se nem isso tiver, usa um nome genérico.
     */
    fun nomeUsuarioAtual(): String {
        val usuario = firebaseAuth.currentUser
        return usuario?.displayName?.takeIf { it.isNotBlank() }
            ?: usuario?.email?.substringBefore("@")
            ?: "Explorador"
    }
}