package io.rinha

import com.zaxxer.hikari.HikariDataSource
import org.json.JSONArray
import org.json.JSONObject

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
            connection.prepareStatement("SELECT * FROM criar_transacao(?, ?, ?, ?)").use { callableStatement ->
                callableStatement.setInt(1, clienteId)
                callableStatement.setInt(2, transacao.valor)
                callableStatement.setString(3, transacao.descricao)
                callableStatement.setString(4, transacao.tipo.tipo)

                callableStatement.executeQuery().use { rs ->
                    rs.next()
                    cliente = Cliente(
                        id = clienteId,
                        saldo = rs.getInt("saldoR"),
                        limite = rs.getInt("limiteR")
                    )
                }
            }
        }
        return cliente
    }

    fun findLast10Transactions(clienteId: Int): JSONArray {
        val ultimasTransacoesResponse = JSONArray()

        getConnection().use { connection ->
            connection.prepareStatement("SELECT * FROM transacao WHERE cliente_id = ? ORDER BY realizada_em DESC LIMIT 10")
                .use { statement ->
                    statement.setInt(1, clienteId)
                    statement.executeQuery().use { rs ->
                        while (rs.next()) {
                            ultimasTransacoesResponse.put(
                                JSONObject()
                                    .put("valor", rs.getInt("valor"))
                                    .put("tipo", rs.getString("tipo"))
                                    .put("descricao", rs.getString("descricao"))
                                    .put("realizada_em", rs.getTimestamp("realizada_em").toLocalDateTime())
                            )
                        }
                    }
                }
        }

        return ultimasTransacoesResponse
    }

}