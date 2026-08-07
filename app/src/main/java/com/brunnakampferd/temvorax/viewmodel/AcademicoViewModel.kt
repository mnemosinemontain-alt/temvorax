package com.brunnakampferd.temvorax.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brunnakampferd.temvorax.data.calendario.CalendarioDispositivo
import com.brunnakampferd.temvorax.data.model.AnexoTcc
import com.brunnakampferd.temvorax.data.model.AreaTarefa
import com.brunnakampferd.temvorax.data.model.BolsaEdital
import com.brunnakampferd.temvorax.data.model.Disciplina
import com.brunnakampferd.temvorax.data.model.EstagioAcademico
import com.brunnakampferd.temvorax.data.model.EventoCalendarioAcademico
import com.brunnakampferd.temvorax.data.model.HoraComplementar
import com.brunnakampferd.temvorax.data.model.Prova
import com.brunnakampferd.temvorax.data.model.RegistroFrequencia
import com.brunnakampferd.temvorax.data.model.Semestre
import com.brunnakampferd.temvorax.data.model.Tcc
import com.brunnakampferd.temvorax.data.model.TipoConclusao
import com.brunnakampferd.temvorax.data.model.TrabalhoAcademico
import com.brunnakampferd.temvorax.data.repository.AcademicoRepository
import com.brunnakampferd.temvorax.data.repository.ProgressoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Estado do envio de um anexo de TCC — separado do resto pra a tela mostrar loading só nisso. */
sealed interface EnvioAnexoEstado {
    data object Ocioso : EnvioAnexoEstado
    data object Enviando : EnvioAnexoEstado
    data class Erro(val mensagem: String) : EnvioAnexoEstado
}

/** Estado da sincronização do Calendário acadêmico com o Calendário nativo do Android. */
sealed interface SincronizacaoCalendarioEstado {
    data object Ociosa : SincronizacaoCalendarioEstado
    data object Sincronizando : SincronizacaoCalendarioEstado
    data class Concluida(val quantidade: Int) : SincronizacaoCalendarioEstado
    data class Erro(val mensagem: String) : SincronizacaoCalendarioEstado
}

/**
 * ViewModel único pra área Acadêmica inteira. As listas SEM dependência de
 * semestre (Calendário, Bolsas, Horas complementares, Estágio, TCC) ficam
 * prontas como StateFlow desde já; as que DEPENDEM de um semestre específico
 * (Disciplinas, Provas, Trabalhos, Frequência) são expostas como função —
 * cada tela chama com o id do semestre/disciplina que está olhando.
 */
class AcademicoViewModel(
    private val repository: AcademicoRepository,
    private val progressoRepository: ProgressoRepository
) : ViewModel() {

    val semestres: StateFlow<List<Semestre>> = repository.observarSemestres()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val calendario: StateFlow<List<EventoCalendarioAcademico>> = repository.observarCalendario()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bolsas: StateFlow<List<BolsaEdital>> = repository.observarBolsas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val horasComplementares: StateFlow<List<HoraComplementar>> = repository.observarHoras()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val estagios: StateFlow<List<EstagioAcademico>> = repository.observarEstagios()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tccs: StateFlow<List<Tcc>> = repository.observarTccs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun observarSemestre(id: Long): Flow<Semestre?> = repository.observarSemestre(id)
    fun observarDisciplinas(semestreId: Long): Flow<List<Disciplina>> = repository.observarDisciplinas(semestreId)
    fun observarProvas(semestreId: Long): Flow<List<Prova>> = repository.observarProvas(semestreId)
    fun observarTrabalhos(semestreId: Long): Flow<List<TrabalhoAcademico>> = repository.observarTrabalhos(semestreId)
    fun observarFrequencia(disciplinaId: Long): Flow<List<RegistroFrequencia>> = repository.observarFrequencia(disciplinaId)

    /** [aoConcluir] recebe o id salvo — usado pra navegar direto pro detalhe recém-criado. */
    fun salvarSemestre(item: Semestre, aoConcluir: (Long) -> Unit = {}) = viewModelScope.launch {
        aoConcluir(repository.salvarSemestre(item))
    }
    fun removerSemestre(item: Semestre) = viewModelScope.launch { repository.removerSemestre(item) }

    fun salvarDisciplina(item: Disciplina) = viewModelScope.launch { repository.salvarDisciplina(item) }
    fun removerDisciplina(item: Disciplina) = viewModelScope.launch { repository.removerDisciplina(item) }

    fun salvarProva(item: Prova) = viewModelScope.launch { repository.salvarProva(item) }
    fun removerProva(item: Prova) = viewModelScope.launch { repository.removerProva(item) }

    fun salvarTrabalho(item: TrabalhoAcademico) = viewModelScope.launch { repository.salvarTrabalho(item) }
    fun alternarTrabalho(item: TrabalhoAcademico) = viewModelScope.launch {
        val concluido = !item.concluido
        repository.salvarTrabalho(item.copy(concluido = concluido))
        if (concluido) {
            progressoRepository.registrarConclusao(TipoConclusao.TRABALHO_ACADEMICO, item.id, AreaTarefa.ACADEMICA)
        } else {
            progressoRepository.desfazerConclusao(TipoConclusao.TRABALHO_ACADEMICO, item.id)
        }
    }
    fun removerTrabalho(item: TrabalhoAcademico) = viewModelScope.launch { repository.removerTrabalho(item) }

    fun salvarFrequencia(item: RegistroFrequencia) = viewModelScope.launch { repository.salvarFrequencia(item) }
    fun removerFrequencia(item: RegistroFrequencia) = viewModelScope.launch { repository.removerFrequencia(item) }

    fun salvarEventoCalendario(item: EventoCalendarioAcademico) = viewModelScope.launch { repository.salvarEventoCalendario(item) }
    fun removerEventoCalendario(item: EventoCalendarioAcademico) = viewModelScope.launch { repository.removerEventoCalendario(item) }

    fun salvarBolsa(item: BolsaEdital) = viewModelScope.launch { repository.salvarBolsa(item) }
    fun removerBolsa(item: BolsaEdital) = viewModelScope.launch { repository.removerBolsa(item) }

    fun salvarHora(item: HoraComplementar) = viewModelScope.launch { repository.salvarHora(item) }
    fun removerHora(item: HoraComplementar) = viewModelScope.launch { repository.removerHora(item) }

    fun salvarEstagio(item: EstagioAcademico) = viewModelScope.launch { repository.salvarEstagio(item) }
    fun removerEstagio(item: EstagioAcademico) = viewModelScope.launch { repository.removerEstagio(item) }

    fun salvarTcc(item: Tcc) = viewModelScope.launch { repository.salvarTcc(item) }
    fun removerTcc(item: Tcc) = viewModelScope.launch { repository.removerTcc(item) }

    // ---- Anexos de TCC ----
    private val _envioAnexo = MutableStateFlow<EnvioAnexoEstado>(EnvioAnexoEstado.Ocioso)
    val envioAnexo: StateFlow<EnvioAnexoEstado> = _envioAnexo.asStateFlow()

    fun observarAnexosTcc(tccId: Long): Flow<List<AnexoTcc>> = repository.observarAnexosTcc(tccId)

    fun anexarArquivoTcc(tccId: Long, uriArquivo: Uri, nomeArquivo: String) {
        viewModelScope.launch {
            _envioAnexo.value = EnvioAnexoEstado.Enviando
            try {
                repository.anexarArquivoTcc(tccId, uriArquivo, nomeArquivo)
                _envioAnexo.value = EnvioAnexoEstado.Ocioso
            } catch (e: Exception) {
                _envioAnexo.value = EnvioAnexoEstado.Erro(
                    e.message ?: "Não foi possível anexar o arquivo. Tenta de novo em instantes."
                )
            }
        }
    }

    fun removerAnexoTcc(anexo: AnexoTcc) = viewModelScope.launch { repository.removerAnexoTcc(anexo) }

    // ---- Sincronização com o Calendário do dispositivo ----
    private val _sincronizacaoCalendario = MutableStateFlow<SincronizacaoCalendarioEstado>(SincronizacaoCalendarioEstado.Ociosa)
    val sincronizacaoCalendario: StateFlow<SincronizacaoCalendarioEstado> = _sincronizacaoCalendario.asStateFlow()

    suspend fun calendariosDoDispositivo(context: Context): List<CalendarioDispositivo.CalendarioDisponivel> =
        withContext(Dispatchers.IO) { CalendarioDispositivo.calendariosGraviaveis(context) }

    /**
     * Sincroniza TODAS as datas do Calendário acadêmico com o calendário [calendarioId]
     * do dispositivo — cria o que ainda não existe lá, atualiza o que já foi sincronizado antes.
     */
    fun sincronizarCalendarioComDispositivo(context: Context, calendarioId: Long) {
        viewModelScope.launch {
            _sincronizacaoCalendario.value = SincronizacaoCalendarioEstado.Sincronizando
            try {
                val eventos = calendario.value
                var quantidade = 0
                withContext(Dispatchers.IO) {
                    eventos.forEach { evento ->
                        val idDispositivo = CalendarioDispositivo.sincronizarEvento(
                            context = context,
                            calendarioId = calendarioId,
                            idExistente = evento.idEventoDispositivo,
                            titulo = evento.titulo,
                            data = evento.data,
                            observacoes = evento.observacoes
                        )
                        if (idDispositivo != evento.idEventoDispositivo) {
                            repository.salvarEventoCalendario(evento.copy(idEventoDispositivo = idDispositivo))
                        }
                        quantidade++
                    }
                }
                _sincronizacaoCalendario.value = SincronizacaoCalendarioEstado.Concluida(quantidade)
            } catch (e: Exception) {
                _sincronizacaoCalendario.value = SincronizacaoCalendarioEstado.Erro(
                    e.message ?: "Não foi possível sincronizar com o calendário do celular."
                )
            }
        }
    }

    fun resetarSincronizacaoCalendario() {
        _sincronizacaoCalendario.value = SincronizacaoCalendarioEstado.Ociosa
    }
}
