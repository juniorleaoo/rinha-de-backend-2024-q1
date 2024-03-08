package io.rinha

import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Status.Companion.UNPROCESSABLE_ENTITY
import org.http4k.core.then
import org.http4k.lens.Path
import org.http4k.lens.nonEmptyString
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.SunHttpLoom
import org.http4k.server.asServer
import org.json.JSONObject
import java.time.LocalDateTime

private val getIdLens = Path.nonEmptyString().map(String::toInt).of("id")

fun isInvalid(request: TransacaoRequest) = request.descricao.isBlank()
        || request.descricao.length > 10
        || (request.tipo != TipoTransacao.CREDITO && request.tipo != TipoTransacao.DEBITO)
        || request.valor <= 0

fun postTransacoes(clienteService: ClienteService): HttpHandler = {
    val clienteId = getIdLens(it)

    if (clienteId < 1 || clienteId > 5) {
        throw HttpException(NOT_FOUND)
    }

    val json = JSONObject(it.bodyString())
    val transacaoRequest = TransacaoRequest(
        valor = Integer.parseInt((json.get("valor") as Number).toString()),
        descricao = json.getString("descricao"),
        tipo = TipoTransacao.from(json.getString("tipo")),
    )

    if (isInvalid(transacaoRequest)) {
        throw HttpException(UNPROCESSABLE_ENTITY)
    }

    val clienteAtualizado =
        clienteService.criarTransacao(
            clienteId = clienteId,
            transacao = Transacao(
                valor = transacaoRequest.valor,
                tipo = transacaoRequest.tipo,
                descricao = transacaoRequest.descricao,
                realizadaEm = LocalDateTime.now()
            )
        )

    Response(OK).body(
        """{
            "limite":${clienteAtualizado.limite},
            "saldo":${clienteAtualizado.saldo}
            }
        """.trimIndent()
    )
}

fun getExtrato(clienteService: ClienteService): HttpHandler = {
    val clienteId = getIdLens(it)
    if (clienteId < 1 || clienteId > 5) {
        throw HttpException(NOT_FOUND)
    }

    val cliente = clienteService.findById(clienteId) ?: throw HttpException(NOT_FOUND)
    val transacoes = clienteService.findLast10Transactions(clienteId)

    Response(OK).body(
        JSONObject()
            .put(
                "saldo", JSONObject()
                    .put("total", cliente.saldo)
                    .put("data_extrato", LocalDateTime.now())
                    .put("limite", cliente.limite)
            )
            .put("ultimas_transacoes", transacoes)
            .toString()
    )
}

fun main() {
    val port = System.getenv("SERVER_PORT")?.toInt() ?: 9999

    val clienteService = ClienteService(connectToDatabase())

    val app: HttpHandler = routes(
        "/clientes/{id}/transacoes" bind Method.POST to postTransacoes(clienteService),
        "/clientes/{id}/extrato" bind GET to getExtrato(clienteService)
    )

    val appFilter = Filter { next ->
        {
            try {
                next(it)
            } catch (e: HttpException) {
                println(e)
                Response(e.status)
            } catch (e: Exception) {
                println(e)
                Response(UNPROCESSABLE_ENTITY)
            }
        }
    }.then(app)
        .asServer(SunHttpLoom(port))
        .start()

    println("Server started on " + appFilter.port())
}
