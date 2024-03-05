package io.rinha

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Status.Companion.UNPROCESSABLE_ENTITY
import org.http4k.core.then
import org.http4k.core.with
import org.http4k.filter.DebuggingFilters
import org.http4k.filter.DebuggingFilters.PrintRequest
import org.http4k.format.Jackson
import org.http4k.lens.Path
import org.http4k.lens.nonEmptyString
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.SunHttp
import org.http4k.server.asServer
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

    val transacaoRequest = transacaoRequestLens.extract(it)

    if (isInvalid(transacaoRequest)) {
        throw HttpException(UNPROCESSABLE_ENTITY)
    }

    val clienteAtualizado = clienteService.criarTransacao(
        clienteId = clienteId,
        transacao = Transacao(
            valor = transacaoRequest.valor,
            tipo = transacaoRequest.tipo,
            descricao = transacaoRequest.descricao,
            realizadaEm = LocalDateTime.now()
        )
    )

    Response(OK).with(
        transacaoResponseLens of TransacaoResponse(
            limite = clienteAtualizado.limite,
            saldo = clienteAtualizado.saldo
        )
    )
}

fun getExtrato(clienteService: ClienteService): HttpHandler = {
    val clienteId = getIdLens(it)
    if (clienteId < 1 || clienteId > 5) {
        throw HttpException(NOT_FOUND)
    }

    val cliente = clienteService.findById(clienteId) ?: throw HttpException(NOT_FOUND)
    val transacoes = clienteService.findLast10Transactions(clienteId)

    val extratoResponse = ExtratoResponse(
        saldo = SaldoResponse(
            total = cliente.saldo,
            LocalDateTime.now(),
            limite = cliente.limite
        ),
        ultimasTransacoes = transacoes
    )

    Response(OK).with(extratoResponseLens of extratoResponse)
}

fun main() {
    Jackson.mapper.propertyNamingStrategy = PropertyNamingStrategies.SnakeCaseStrategy()
    Jackson.mapper.disable(DeserializationFeature.ACCEPT_FLOAT_AS_INT)

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

    val port = System.getenv("SERVER_PORT")?.toInt() ?: 8080
    val server = PrintRequest()
        .then(appFilter)
        .asServer(SunHttp(port))
        .start()

    println("Server started on " + server.port())
}
