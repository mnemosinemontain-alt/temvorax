package com.brunnakampferd.temvorax.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

enum class NivelEnsino(val rotulo: String) {
    TECNICO("Técnico"),
    SUPERIOR("Superior")
}

enum class Turno(val rotulo: String) {
    MANHA("Manhã"),
    TARDE("Tarde"),
    NOITE("Noite")
}

/**
 * Dados de perfil do usuário — sempre UMA linha só (id fixo = 1), editada
 * pelo menu lateral de Perfil. Fica separado do FirebaseAuth (que só guarda
 * e-mail/nome do login) porque esses campos são específicos do Temvorax.
 *
 * @param fotoUrl link da foto de perfil depois do upload pro Firebase Storage
 *   (null enquanto não tem foto, ou durante o upload).
 */
@Entity(tableName = "perfil")
data class Perfil(
    @PrimaryKey val id: Int = 1,
    // Profissional
    val profissao: String = "",
    val empresa: String = "",
    val cargo: String = "",
    // Acadêmica
    val nomeCompleto: String = "",
    val dataNascimento: LocalDate? = null,
    val instituicaoEnsino: String = "",
    val curso: String = "",
    val nivelEnsino: NivelEnsino? = null,
    val semestrePeriodo: String = "",
    val turno: Turno? = null,
    val cidade: String = "",
    // Foto
    val fotoUrl: String? = null
)
