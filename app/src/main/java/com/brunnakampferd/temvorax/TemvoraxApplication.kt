package com.brunnakampferd.temvorax

import android.app.Application
import com.brunnakampferd.temvorax.data.local.AppDatabase

/**
 * Application customizada só pra segurar UMA instância do banco de dados
 * (Room) durante a vida inteira do app — os repositories pegam essa mesma
 * instância em vez de cada um abrir seu próprio banco.
 */
class TemvoraxApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.obter(this) }
}
