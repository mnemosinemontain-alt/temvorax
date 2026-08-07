package com.brunnakampferd.temvorax.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.brunnakampferd.temvorax.TemvoraxApplication
import com.brunnakampferd.temvorax.data.repository.AcademicoRepository
import com.brunnakampferd.temvorax.data.repository.PerfilRepository
import com.brunnakampferd.temvorax.data.repository.PessoalRepository
import com.brunnakampferd.temvorax.data.repository.ProfissionalRepository
import com.brunnakampferd.temvorax.data.repository.ProgressoRepository
import com.brunnakampferd.temvorax.data.repository.TarefaRepository
import com.brunnakampferd.temvorax.viewmodel.AcademicoViewModel
import com.brunnakampferd.temvorax.viewmodel.AgendaDoDiaViewModel
import com.brunnakampferd.temvorax.viewmodel.PerfilViewModel
import com.brunnakampferd.temvorax.viewmodel.PessoalViewModel
import com.brunnakampferd.temvorax.viewmodel.PlanejamentoViewModel
import com.brunnakampferd.temvorax.viewmodel.ProfissionalViewModel
import com.brunnakampferd.temvorax.viewmodel.ProgressoViewModel
import com.brunnakampferd.temvorax.viewmodel.TarefaViewModel
import java.time.LocalDate
import java.time.LocalTime

/**
 * Funções compostas que montam cada ViewModel já com seu(s) repository(ies),
 * pegando o banco (Room) da Application. Centralizar essa fiação aqui evita
 * espalhar `LocalContext.current.applicationContext as TemvoraxApplication`
 * por cada tela.
 */

@Composable
private fun aplicacao(): TemvoraxApplication =
    LocalContext.current.applicationContext as TemvoraxApplication

@Composable
fun tarefaViewModel(): TarefaViewModel {
    val app = aplicacao()
    return viewModel(factory = viewModelFactory {
        initializer {
            val db = app.database
            TarefaViewModel(TarefaRepository(db.tarefaDao()), ProgressoRepository(db.registroConclusaoDao()))
        }
    })
}

@Composable
fun academicoViewModel(): AcademicoViewModel {
    val app = aplicacao()
    return viewModel(factory = viewModelFactory {
        initializer {
            val db = app.database
            AcademicoViewModel(
                AcademicoRepository(
                    semestreDao = db.semestreDao(),
                    disciplinaDao = db.disciplinaDao(),
                    provaDao = db.provaDao(),
                    trabalhoDao = db.trabalhoAcademicoDao(),
                    frequenciaDao = db.registroFrequenciaDao(),
                    calendarioDao = db.eventoCalendarioAcademicoDao(),
                    bolsaDao = db.bolsaEditalDao(),
                    horasDao = db.horaComplementarDao(),
                    estagioDao = db.estagioAcademicoDao(),
                    tccDao = db.tccDao(),
                    anexoTccDao = db.anexoTccDao()
                ),
                ProgressoRepository(db.registroConclusaoDao())
            )
        }
    })
}

@Composable
fun profissionalViewModel(): ProfissionalViewModel {
    val app = aplicacao()
    return viewModel(factory = viewModelFactory {
        initializer {
            val db = app.database
            ProfissionalViewModel(
                ProfissionalRepository(db.expedienteDao(), db.compromissoProfissionalDao(), db.atividadeProfissionalDao()),
                ProgressoRepository(db.registroConclusaoDao())
            )
        }
    })
}

@Composable
fun pessoalViewModel(): PessoalViewModel {
    val app = aplicacao()
    return viewModel(factory = viewModelFactory {
        initializer {
            val db = app.database
            PessoalViewModel(PessoalRepository(db.tarefaDiariaDao(), db.compromissoPessoalDao()), ProgressoRepository(db.registroConclusaoDao()))
        }
    })
}

@Composable
fun progressoViewModel(): ProgressoViewModel {
    val app = aplicacao()
    return viewModel(factory = viewModelFactory {
        initializer { ProgressoViewModel(ProgressoRepository(app.database.registroConclusaoDao())) }
    })
}

@Composable
fun perfilViewModel(): PerfilViewModel {
    val app = aplicacao()
    return viewModel(factory = viewModelFactory {
        initializer { PerfilViewModel(PerfilRepository(app.database.perfilDao(), app)) }
    })
}

@Composable
fun agendaDoDiaViewModel(): AgendaDoDiaViewModel {
    val app = aplicacao()
    return viewModel(factory = viewModelFactory {
        initializer {
            val db = app.database
            AgendaDoDiaViewModel(
                tarefaRepository = TarefaRepository(db.tarefaDao()),
                academicoRepository = AcademicoRepository(
                    semestreDao = db.semestreDao(),
                    disciplinaDao = db.disciplinaDao(),
                    provaDao = db.provaDao(),
                    trabalhoDao = db.trabalhoAcademicoDao(),
                    frequenciaDao = db.registroFrequenciaDao(),
                    calendarioDao = db.eventoCalendarioAcademicoDao(),
                    bolsaDao = db.bolsaEditalDao(),
                    horasDao = db.horaComplementarDao(),
                    estagioDao = db.estagioAcademicoDao(),
                    tccDao = db.tccDao(),
                    anexoTccDao = db.anexoTccDao()
                ),
                profissionalRepository = ProfissionalRepository(db.expedienteDao(), db.compromissoProfissionalDao(), db.atividadeProfissionalDao()),
                pessoalRepository = PessoalRepository(db.tarefaDiariaDao(), db.compromissoPessoalDao()),
                hoje = LocalDate.now(),
                agora = LocalTime.now()
            )
        }
    })
}

@Composable
fun planejamentoViewModel(): PlanejamentoViewModel {
    val app = aplicacao()
    return viewModel(factory = viewModelFactory {
        initializer {
            val db = app.database
            PlanejamentoViewModel(
                AcademicoRepository(
                    semestreDao = db.semestreDao(),
                    disciplinaDao = db.disciplinaDao(),
                    provaDao = db.provaDao(),
                    trabalhoDao = db.trabalhoAcademicoDao(),
                    frequenciaDao = db.registroFrequenciaDao(),
                    calendarioDao = db.eventoCalendarioAcademicoDao(),
                    bolsaDao = db.bolsaEditalDao(),
                    horasDao = db.horaComplementarDao(),
                    estagioDao = db.estagioAcademicoDao(),
                    tccDao = db.tccDao(),
                    anexoTccDao = db.anexoTccDao()
                ),
                ProfissionalRepository(db.expedienteDao(), db.compromissoProfissionalDao(), db.atividadeProfissionalDao()),
                PessoalRepository(db.tarefaDiariaDao(), db.compromissoPessoalDao())
            )
        }
    })
}
