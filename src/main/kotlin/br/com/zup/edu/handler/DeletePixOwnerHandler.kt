package br.com.zup.edu.handler

import io.grpc.Status
import javax.inject.Singleton

@Singleton
class DeletePixOwnerHandler: ExceptionHandler<PixOwnerException> {

    override fun handle(e: PixOwnerException): ExceptionHandler.StatusWithDetails {
        return ExceptionHandler.StatusWithDetails(
            Status.PERMISSION_DENIED
                .withDescription(e.message)
                .withCause(e)
        )
    }

    override fun supports(e: Exception): Boolean {
        return e is PixOwnerException
    }
}