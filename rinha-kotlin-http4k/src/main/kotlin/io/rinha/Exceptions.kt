package io.rinha

import org.http4k.core.Status

open class HttpException(val status: Status, message: String = status.description) : RuntimeException(message)