package io.rinha

import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.Executors

data class Addr(val port: Int, val host: String)

class RoundRobin(
    val addrs: List<Addr>,
    var counter: Int = 0
) {

    fun next(): Addr {
        val addr = addrs[counter % addrs.size]
        counter++
        return addr
    }

}

class Proxy(
    private val clientSocket: Socket,
    private val backendSocket: Socket
) : Runnable {

    override fun run() {
        try {
            proxy(clientSocket, backendSocket)
        } catch (e: IOException) {
            e.printStackTrace()
            throw RuntimeException()
        }
    }

    private fun proxy(clientSocket: Socket, backendSocket: Socket) {
        Thread.ofVirtual().start {
            try {
                clientSocket.getInputStream()
                    .copyTo(backendSocket.getOutputStream())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        Thread.ofVirtual().start {
            try {
                backendSocket.getInputStream()
                    .copyTo(clientSocket.getOutputStream())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

}

fun main() {
    val serverSocket = ServerSocket(9999)
    val executor = Executors.newVirtualThreadPerTaskExecutor()

    val loadBalancer = RoundRobin(
        addrs = listOf(
            Addr(3000, "api1"),
            Addr(3001, "api2")
        )
    )

    while (true) {
        val socket = serverSocket.accept()
        val nextAddr = loadBalancer.next()
        executor.submit(
            Proxy(
                clientSocket = socket,
                backendSocket = Socket(nextAddr.host, nextAddr.port)
            )
        )
    }

}