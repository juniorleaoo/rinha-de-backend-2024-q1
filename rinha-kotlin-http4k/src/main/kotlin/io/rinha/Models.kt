package io.rinha

import com.fasterxml.jackson.annotation.JsonProperty
import org.http4k.core.Body
import org.http4k.format.Jackson.auto
import java.time.LocalDateTime

data class Cliente(
    val id: Int,
    val limite: Int,
    val saldo: Int,
    val transacoes: List<Transacao> = mutableListOf()
)

data class Transacao(
    val valor: Int,
    val tipo: TipoTransacao,
    val descricao: String,
    val realizadaEm: LocalDateTime
)

data class SaldoResponse(
    val total: Int,
    val dataExtrato: LocalDateTime,
    val limite: Int
)

enum class TipoTransacao(val tipo: String) {
    @JsonProperty("c")
    CREDITO("c"),

    @JsonProperty("d")
    DEBITO("d");

    companion object {
        fun from(tipo: String) = TipoTransacao.entries.find { it.tipo == tipo } ?: CREDITO
    }
}

data class ExtratoResponse(
    val saldo: SaldoResponse,
    val ultimasTransacoes: List<Transacao>
)

val extratoResponseLens = Body.auto<ExtratoResponse>().toLens()

data class TransacaoRequest(
    val valor: Int,
    val tipo: TipoTransacao,
    val descricao: String,
)

val transacaoRequestLens = Body.auto<TransacaoRequest>().toLens()

data class TransacaoResponse(
    val limite: Int,
    val saldo: Int,
)

val transacaoResponseLens = Body.auto<TransacaoResponse>().toLens()
