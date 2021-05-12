package br.com.zup.edu.handler

import io.grpc.Status
import javax.inject.Singleton

@Singleton
class PixDuplicadoExceptionHandler: ExceptionHandler<PixDuplicadoException> {

    override fun handle(e: PixDuplicadoException): ExceptionHandler.StatusWithDetails {
        return ExceptionHandler.StatusWithDetails(
            Status.ALREADY_EXISTS
                .withDescription(e.message)
                .withCause(e)
        )
    }

    override fun supports(e: Exception): Boolean {
        return e is PixDuplicadoException
    }
}