package com.brunnakampferd.temvorax.data.repository

import android.net.Uri
import com.brunnakampferd.temvorax.data.local.dao.AnexoTccDao
import com.brunnakampferd.temvorax.data.local.dao.BolsaEditalDao
import com.brunnakampferd.temvorax.data.local.dao.DisciplinaDao
import com.brunnakampferd.temvorax.data.local.dao.EstagioAcademicoDao
import com.brunnakampferd.temvorax.data.local.dao.EventoCalendarioAcademicoDao
import com.brunnakampferd.temvorax.data.local.dao.HoraComplementarDao
import com.brunnakampferd.temvorax.data.local.dao.ProvaDao
import com.brunnakampferd.temvorax.data.local.dao.RegistroFrequenciaDao
import com.brunnakampferd.temvorax.data.local.dao.SemestreDao
import com.brunnakampferd.temvorax.data.local.dao.TccDao
import com.brunnakampferd.temvorax.data.local.dao.TrabalhoAcademicoDao
import com.brunnakampferd.temvorax.data.model.AnexoTcc
import com.brunnakampferd.temvorax.data.model.BolsaEdital
import com.brunnakampferd.temvorax.data.model.Disciplina
import com.brunnakampferd.temvorax.data.model.EstagioAcademico
import com.brunnakampferd.temvorax.data.model.EventoCalendarioAcademico
import com.brunnakampferd.temvorax.data.model.HoraComplementar
import com.brunnakampferd.temvorax.data.model.Prova
import com.brunnakampferd.temvorax.data.model.RegistroFrequencia
import com.brunnakampferd.temvorax.data.model.Semestre
import com.brunnakampferd.temvorax.data.model.Tcc
import com.brunnakampferd.temvorax.data.model.TrabalhoAcademico
import com.brunnakampferd.temvorax.domain.ItemAgenda
import com.brunnakampferd.temvorax.domain.itensAcademicosDoDia
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.util.UUID

/** Repository único pra área Acadêmica inteira — junta os 11 DAOs dela. */
class AcademicoRepository(
    private val semestreDao: SemestreDao,
    private val disciplinaDao: DisciplinaDao,
    private val provaDao: ProvaDao,
    private val trabalhoDao: TrabalhoAcademicoDao,
    private val frequenciaDao: RegistroFrequenciaDao,
    private val calendarioDao: EventoCalendarioAcademicoDao,
    private val bolsaDao: BolsaEditalDao,
    private val horasDao: HoraComplementarDao,
    private val estagioDao: EstagioAcademicoDao,
    private val tccDao: TccDao,
    private val anexoTccDao: AnexoTccDao,
    private val storage: FirebaseStorage = FirebaseStorage.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    /**
     * Agenda do dia da área Acadêmica (Aulas + Provas + Trabalhos de hoje, só
     * de semestres ativos) — usada pra montar a aba "Hoje" da Home, cruzando
     * as 4 fontes reativamente.
     */
    fun observarItensAcademicosDoDia(hoje: LocalDate): Flow<List<ItemAgenda>> =
        combine(observarSemestres(), disciplinaDao.observarTodas(), provaDao.observarTodas(), trabalhoDao.observarTodos()) {
            semestres, disciplinas, provas, trabalhos ->
            itensAcademicosDoDia(hoje, semestres, disciplinas, provas, trabalhos)
        }

    // ---- Semestres ----
    fun observarSemestres(): Flow<List<Semestre>> = semestreDao.observarTodos()
    fun observarSemestre(id: Long): Flow<Semestre?> = semestreDao.observarPorId(id)
    /** Retorna o id do semestre salvo — o de sempre se foi edição, ou o recém-gerado se foi inserção. */
    suspend fun salvarSemestre(item: Semestre): Long =
        if (item.id == 0L) semestreDao.inserir(item) else { semestreDao.atualizar(item); item.id }
    suspend fun removerSemestre(item: Semestre) = semestreDao.remover(item)

    // ---- Disciplinas ----
    fun observarDisciplinas(semestreId: Long): Flow<List<Disciplina>> = disciplinaDao.observarPorSemestre(semestreId)
    suspend fun salvarDisciplina(item: Disciplina) {
        if (item.id == 0L) disciplinaDao.inserir(item) else disciplinaDao.atualizar(item)
    }
    suspend fun removerDisciplina(item: Disciplina) = disciplinaDao.remover(item)

    // ---- Provas ----
    fun observarProvas(semestreId: Long): Flow<List<Prova>> = provaDao.observarPorSemestre(semestreId)
    suspend fun salvarProva(item: Prova) {
        if (item.id == 0L) provaDao.inserir(item) else provaDao.atualizar(item)
    }
    suspend fun removerProva(item: Prova) = provaDao.remover(item)

    // ---- Trabalhos acadêmicos ----
    fun observarTrabalhos(semestreId: Long): Flow<List<TrabalhoAcademico>> = trabalhoDao.observarPorSemestre(semestreId)
    suspend fun salvarTrabalho(item: TrabalhoAcademico) {
        if (item.id == 0L) trabalhoDao.inserir(item) else trabalhoDao.atualizar(item)
    }
    suspend fun removerTrabalho(item: TrabalhoAcademico) = trabalhoDao.remover(item)

    // ---- Frequência ----
    fun observarFrequencia(disciplinaId: Long): Flow<List<RegistroFrequencia>> = frequenciaDao.observarPorDisciplina(disciplinaId)
    suspend fun salvarFrequencia(item: RegistroFrequencia) {
        if (item.id == 0L) frequenciaDao.inserir(item) else frequenciaDao.atualizar(item)
    }
    suspend fun removerFrequencia(item: RegistroFrequencia) = frequenciaDao.remover(item)

    // ---- Calendário acadêmico ----
    fun observarCalendario(): Flow<List<EventoCalendarioAcademico>> = calendarioDao.observarTodos()
    suspend fun salvarEventoCalendario(item: EventoCalendarioAcademico) {
        if (item.id == 0L) calendarioDao.inserir(item) else calendarioDao.atualizar(item)
    }
    suspend fun removerEventoCalendario(item: EventoCalendarioAcademico) = calendarioDao.remover(item)

    // ---- Bolsas e editais ----
    fun observarBolsas(): Flow<List<BolsaEdital>> = bolsaDao.observarTodas()
    suspend fun salvarBolsa(item: BolsaEdital) {
        if (item.id == 0L) bolsaDao.inserir(item) else bolsaDao.atualizar(item)
    }
    suspend fun removerBolsa(item: BolsaEdital) = bolsaDao.remover(item)

    // ---- Horas complementares ----
    fun observarHoras(): Flow<List<HoraComplementar>> = horasDao.observarTodas()
    suspend fun salvarHora(item: HoraComplementar) {
        if (item.id == 0L) horasDao.inserir(item) else horasDao.atualizar(item)
    }
    suspend fun removerHora(item: HoraComplementar) = horasDao.remover(item)

    // ---- Estágio acadêmico ----
    fun observarEstagios(): Flow<List<EstagioAcademico>> = estagioDao.observarTodos()
    suspend fun salvarEstagio(item: EstagioAcademico) {
        if (item.id == 0L) estagioDao.inserir(item) else estagioDao.atualizar(item)
    }
    suspend fun removerEstagio(item: EstagioAcademico) = estagioDao.remover(item)

    // ---- TCC ----
    fun observarTccs(): Flow<List<Tcc>> = tccDao.observarTodos()
    suspend fun salvarTcc(item: Tcc) {
        if (item.id == 0L) tccDao.inserir(item) else tccDao.atualizar(item)
    }
    suspend fun removerTcc(item: Tcc) = tccDao.remover(item)

    // ---- Anexos de TCC ----
    fun observarAnexosTcc(tccId: Long): Flow<List<AnexoTcc>> = anexoTccDao.observarPorTcc(tccId)

    /**
     * Sobe o arquivo escolhido pro Firebase Storage (num caminho exclusivo do
     * usuário + TCC, nome prefixado com um UUID pra nunca colidir) e só depois
     * salva a referência (nome + URL de download) no Room.
     */
    suspend fun anexarArquivoTcc(tccId: Long, uriArquivo: Uri, nomeArquivo: String) {
        val uid = auth.currentUser?.uid ?: error("Nenhum usuário logado.")
        val referencia = storage.reference.child("anexos_tcc/$uid/$tccId/${UUID.randomUUID()}_$nomeArquivo")
        referencia.putFile(uriArquivo).await()
        val url = referencia.downloadUrl.await().toString()
        anexoTccDao.inserir(AnexoTcc(tccId = tccId, nomeArquivo = nomeArquivo, url = url))
    }

    /** Remove o registro local mesmo se apagar do Storage falhar (ex.: sem internet) — não trava o usuário. */
    suspend fun removerAnexoTcc(anexo: AnexoTcc) {
        runCatching { storage.getReferenceFromUrl(anexo.url).delete().await() }
        anexoTccDao.remover(anexo)
    }
}
