package com.brunnakampferd.temvorax.data.cloudinary

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

/**
 * Upload de imagem pro Cloudinary — usado no lugar do Firebase Storage (que
 * passou a exigir plano pago/Blaze mesmo dentro da cota gratuita a partir de
 * out/2024; decidimos não migrar pra lá por enquanto).
 *
 * Usa "unsigned upload": um POST multipart direto do celular pro Cloudinary,
 * sem servidor no meio, autenticado só pelo nome do preset — é o jeito que o
 * próprio Cloudinary recomenda pra apps mobile fazerem upload direto. Nem o
 * Cloud name nem o nome do preset são segredo (o preset "unsigned" foi feito
 * de propósito pra ficar embutido no app); a segurança contra abuso vem das
 * restrições configuradas NO PAINEL do Cloudinary pro preset "fotos_perfil"
 * (pasta, tamanho máximo, formatos aceitos) — não daqui do código.
 */
object ClienteCloudinary {
    private const val CLOUD_NAME = "lklzc6l4"
    private const val UPLOAD_PRESET = "fotos_perfil"

    private val httpClient = OkHttpClient()

    /**
     * Sobe o arquivo apontado por [uri] pro Cloudinary e devolve a URL pública
     * (`secure_url`) da imagem. Lança exceção se não conseguir ler o arquivo
     * ou se o Cloudinary recusar o upload (preset errado, Cloud name errado,
     * arquivo fora das restrições configuradas no preset etc.).
     */
    suspend fun enviarImagem(context: Context, uri: Uri, nomeArquivo: String = "foto.jpg"): String =
        withContext(Dispatchers.IO) {
            val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                ?: error("Não foi possível ler o arquivo escolhido.")

            val corpo = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("upload_preset", UPLOAD_PRESET)
                .addFormDataPart("file", nomeArquivo, bytes.toRequestBody("image/jpeg".toMediaType()))
                .build()

            val requisicao = Request.Builder()
                .url("https://api.cloudinary.com/v1_1/$CLOUD_NAME/image/upload")
                .post(corpo)
                .build()

            httpClient.newCall(requisicao).execute().use { resposta ->
                val corpoResposta = resposta.body?.string().orEmpty()
                if (!resposta.isSuccessful) {
                    error("Cloudinary recusou o upload (código ${resposta.code}): $corpoResposta")
                }
                JSONObject(corpoResposta).getString("secure_url")
            }
        }
}
