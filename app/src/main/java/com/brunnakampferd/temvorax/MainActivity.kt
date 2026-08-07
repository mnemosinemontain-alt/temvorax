package com.brunnakampferd.temvorax

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.brunnakampferd.temvorax.ui.navigation.Rotas
import com.brunnakampferd.temvorax.ui.navigation.TemvoraxNavHost
import com.brunnakampferd.temvorax.ui.theme.TemvoraxTheme
import com.brunnakampferd.temvorax.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TemvoraxTheme {

                val navController = rememberNavController()

                // ViewModel ÚNICO e compartilhado entre login por e-mail/senha e Google.
                // É importante ser o mesmo, pois é ele quem guarda o resultado (sucesso/erro)
                // e avisa a AuthScreen quando pode navegar pra Home, não importa qual caminho
                // o usuário escolheu pra entrar.
                val authViewModel: AuthViewModel = viewModel()

                // Contexto necessário pra montar as opções do Google Sign-In.
                val context = LocalContext.current

                // Configuração do Google Sign-In. "remember" garante que isso só é montado
                // uma vez, não a cada recomposição da tela.
                val googleSignInClient = remember {
                    val opcoes = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        // default_web_client_id é gerado automaticamente pelo plugin do
                        // Google Services a partir do google-services.json — não precisamos
                        // (e não devemos) digitar esse ID na mão em lugar nenhum.
                        .requestIdToken(context.getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build()
                    GoogleSignIn.getClient(context, opcoes)
                }

                // "Launcher" que abre a telinha do Android de escolher a conta Google,
                // e recebe o resultado de volta quando o usuário escolhe (ou cancela).
                val googleSignInLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()
                ) { resultado ->
                    val task = GoogleSignIn.getSignedInAccountFromIntent(resultado.data)
                    try {
                        val conta = task.getResult(ApiException::class.java)
                        val idToken = conta.idToken
                        if (idToken != null) {
                            // Manda o token pro ViewModel, que repassa pro Firebase confirmar.
                            authViewModel.entrarComGoogle(idToken)
                        }
                    } catch (e: ApiException) {
                        // Usuário cancelou a escolha de conta, ou algo falhou no processo.
                        // Por enquanto não fazemos nada aqui — o usuário só continua na
                        // tela de auth e pode tentar de novo.
                    }
                }

                // O Firebase Auth mantém a sessão salva entre aberturas do app por padrão —
                // se já tem alguém logado, pula Splash/Welcome/Login e vai direto pra Home.
                val destinoInicial = remember {
                    if (authViewModel.usuarioJaLogado()) Rotas.HOME else Rotas.SPLASH
                }

                TemvoraxNavHost(
                    navController = navController,
                    authViewModel = authViewModel,
                    startDestination = destinoInicial,
                    onGoogleSignInClick = { googleSignInLauncher.launch(googleSignInClient.signInIntent) }
                )
            }
        }
    }
}
