package io.rinha

import org.http4k.core.Status.Companion.UNPROCESSABLE_ENTITY
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

enum class TipoTransacao(val tipo: String) {
    CREDITO("c"),
    DEBITO("d");

    companion object {
        fun from(tipo: String): TipoTransacao {
            return TipoTransacao.entries.find { it.tipo == tipo } ?: throw HttpException(UNPROCESSABLE_ENTITY)
        }
    }
}

data class TransacaoRequest(
    val valor: Int,
    val tipo: TipoTransacao,
    val descricao: String,
)

data class TransacaoResponse(
    val limite: Int,
    val saldo: Int,
)
