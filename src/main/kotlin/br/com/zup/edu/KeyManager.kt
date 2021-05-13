package br.com.zup.edu

import br.com.zup.edu.dto.toDto
import br.com.zup.edu.handler.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Singleton

@Singleton
@OpenClass
class KeyManager(
    val service: KeyService
) : KeymanagergrpcServiceGrpc.KeymanagergrpcServiceImplBase() {

    @ErrorHandler
    override fun cria(request: CreateKeyRequest, responseObserver: StreamObserver<CreateKeyResponse>) {
        val keyRequest = request.toDto()
        val registra = service.registra(keyRequest)

        responseObserver.onNext(registra)
        responseObserver.onCompleted()
    }

    @ErrorHandler
    override fun deleta(request: DeleteKeyRequest, responseObserver: StreamObserver<DeleteKeyResponse>) {
        service.deleta(request.toDto())

        val response = DeleteKeyResponse.newBuilder().build()

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }
}