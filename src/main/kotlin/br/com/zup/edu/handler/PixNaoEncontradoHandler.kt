package br.com.zup.edu.handler

import io.grpc.Status
import javax.inject.Singleton

@Singleton
class PixNaoEncontradoHandler: ExceptionHandler<PixNaoEncontradoException> {
    override fun handle(e: PixNaoEncontradoException): ExceptionHandler.StatusWithDetails {
        return ExceptionHandler.StatusWithDetails(
            Status.NOT_FOUND
                .withDescription(e.message)
                .withCause(e)
        )
    }

    override fun supports(e: Exception): Boolean = e is PixNaoEncontradoException
}