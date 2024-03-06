package io.rinha

import com.zaxxer.hikari.HikariDataSource
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.UNPROCESSABLE_ENTITY
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDateTime

class ClienteService(
    private val datasource: HikariDataSource
) {

    private fun getConnection() = datasource.connection

    fun findById(clienteId: Int): Cliente? {
        getConnection().use { connection ->
            connection.prepareStatement("SELECT * FROM cliente WHERE id = ?").use { statement ->
                statement.setInt(1, clienteId)
                statement.executeQuery().use { resultSet ->
                    if (resultSet.next()) {
                        return Cliente(
                            id = resultSet.getInt("id"),
                            saldo = resultSet.getInt("saldo"),
                            limite = resultSet.getInt("limite")
                        )
                    } else {
                        return null
                    }
                }
            }
        }
    }

    fun criarTransacao(clienteId: Int, transacao: Transacao): Cliente {
        var cliente: Cliente
        getConnection().use { connection ->
            connection.autoCommit = false
            connection.prepareStatement("SELECT * FROM cliente WHERE id = ? FOR UPDATE").use { ps ->
                ps.setInt(1, clienteId)
                ps.executeQuery().use { rs ->
                    if (rs.next()) {
                        cliente = Cliente(
                            id = rs.getInt("id"),
                            saldo = rs.getInt("saldo"),
                            limite = rs.getInt("limite")
                        )
                    } else {
                        throw HttpException(NOT_FOUND)
                    }
                }
            }

            var saldo = cliente.saldo
            if (transacao.tipo == TipoTransacao.DEBITO) {
                if ((cliente.saldo + cliente.limite) >= transacao.valor) {
                    saldo -= transacao.valor
                } else {
                    throw HttpException(UNPROCESSABLE_ENTITY)
                }
            } else {
                saldo += transacao.valor
            }

            connection.prepareStatement("INSERT INTO transacao (cliente_id, valor, tipo, descricao) VALUES (?, ?, ?, ?)").use { ps ->
                ps.setInt(1, clienteId)
                ps.setInt(2, transacao.valor)
                ps.setString(3, transacao.tipo.tipo)
                ps.setString(4, transacao.descricao)
                ps.executeUpdate()
            }

            connection.prepareStatement("UPDATE cliente SET saldo = ? WHERE id = ? RETURNING saldo, limite").use { ps ->
                ps.setInt(1, saldo)
                ps.setInt(2, clienteId)
                ps.executeQuery().use { rs ->
                    rs.next()
                    cliente = Cliente(
                        id = clienteId,
                        limite = rs.getInt("limite"),
                        saldo = rs.getInt("saldo"),
                    )
                }
            }

            connection.commit()
        }
        return cliente
    }

    fun findLast10Transactions(clienteId: Int): JSONArray {
        val ultimasTransacoesResponse = JSONArray()

        getConnection().use { connection ->
            connection.prepareStatement("SELECT * FROM transacao WHERE cliente_id = ? ORDER BY realizada_em DESC LIMIT 10").use { statement ->
                statement.setInt(1, clienteId)
                statement.executeQuery().use { rs ->
                    while (rs.next()) {
                        ultimasTransacoesResponse.put(
                            JSONObject()
                            .put("valor", rs.getInt("valor"))
                            .put("tipo", rs.getString("tipo"))
                            .put("descricao", rs.getString("descricao"))
                            .put("realizada_em",  rs.getTimestamp("realizada_em").toLocalDateTime())
                        )
                    }
                }
            }
        }

        return ultimasTransacoesResponse
    }


}