package com.brunnakampferd.temvorax.ui.screens.academico

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.brunnakampferd.temvorax.data.model.EstagioAcademico
import com.brunnakampferd.temvorax.ui.components.CartaoItemGenerico
import com.brunnakampferd.temvorax.ui.components.ListaGenericaScaffold
import com.brunnakampferd.temvorax.ui.components.TelaFormularioScaffold
import com.brunnakampferd.temvorax.ui.components.form.CampoData
import com.brunnakampferd.temvorax.ui.components.form.CampoNumero
import com.brunnakampferd.temvorax.ui.components.form.CampoTexto
import com.brunnakampferd.temvorax.ui.theme.AzulProfissional

@Composable
fun EstagioAcademicoScreen(
    estagios: List<EstagioAcademico>,
    onVoltar: () -> Unit,
    onAdicionar: () -> Unit,
    onEditar: (EstagioAcademico) -> Unit,
    onExcluir: (EstagioAcademico) -> Unit,
    modifier: Modifier = Modifier
) {
    ListaGenericaScaffold(
        titulo = "Estágio acadêmico",
        itens = estagios,
        chave = { it.id },
        onVoltar = onVoltar,
        onAdicionar = onAdicionar,
        mensagemVazia = "Nenhum estágio cadastrado ainda.",
        modifier = modifier
    ) { estagio ->
        CartaoItemGenerico(
            titulo = estagio.empresa,
            subtitulo = listOfNotNull(
                estagio.cargo.takeIf { it.isNotBlank() },
                "${estagio.totalHorasCumpridas}h de ${estagio.cargaHorariaSemanal}h/semana"
            ).joinToString(" • "),
            corLateral = AzulProfissional,
            onClick = { onEditar(estagio) },
            onExcluir = { onExcluir(estagio) }
        )
    }
}

@Composable
fun EstagioAcademicoFormScreen(
    estagio: EstagioAcademico?,
    onVoltar: () -> Unit,
    onSalvar: (EstagioAcademico) -> Unit,
    modifier: Modifier = Modifier,
    onExcluir: (() -> Unit)? = null
) {
    var empresa by remember { mutableStateOf(estagio?.empresa ?: "") }
    var cargo by remember { mutableStateOf(estagio?.cargo ?: "") }
    var supervisor by remember { mutableStateOf(estagio?.supervisor ?: "") }
    var dataInicio by remember { mutableStateOf(estagio?.dataInicio) }
    var dataTermino by remember { mutableStateOf(estagio?.dataTerminoPrevista) }
    var cargaSemanal by remember { mutableStateOf(estagio?.cargaHorariaSemanal ?: 0.0) }
    var horasCumpridas by remember { mutableStateOf(estagio?.totalHorasCumpridas ?: 0.0) }
    var observacoes by remember { mutableStateOf(estagio?.observacoes ?: "") }

    TelaFormularioScaffold(
        titulo = if (estagio == null) "Novo estágio" else "Editar estágio",
        onVoltar = onVoltar,
        onExcluir = onExcluir,
        onSalvar = {
            onSalvar(
                (estagio ?: EstagioAcademico(empresa = empresa)).copy(
                    empresa = empresa,
                    cargo = cargo,
                    supervisor = supervisor,
                    dataInicio = dataInicio,
                    dataTerminoPrevista = dataTermino,
                    cargaHorariaSemanal = cargaSemanal,
                    totalHorasCumpridas = horasCumpridas,
                    observacoes = observacoes
                )
            )
        },
        modifier = modifier
    ) {
        CampoTexto(empresa, { empresa = it }, "Empresa/instituição")
        CampoTexto(cargo, { cargo = it }, "Cargo/função")
        CampoTexto(supervisor, { supervisor = it }, "Supervisor(a)/orientador(a) de estágio")
        CampoData(dataInicio, { dataInicio = it }, "Data de início")
        CampoData(dataTermino, { dataTermino = it }, "Data de término prevista")
        CampoNumero(cargaSemanal, { cargaSemanal = it }, "Carga horária semanal")
        CampoNumero(horasCumpridas, { horasCumpridas = it }, "Total de horas cumpridas")
        CampoTexto(observacoes, { observacoes = it }, "Observações", linhasMultiplas = true)
    }
}
