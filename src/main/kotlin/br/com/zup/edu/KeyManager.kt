package br.com.zup.edu

import br.com.zup.edu.dto.toModel
import br.com.zup.edu.handler.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Singleton

@Singleton
@OpenClass
class KeyManager(
//    val clientERP: ERPClient,
//    val clientBacen: BacenClient,
//    val validator: Validator,
//    val repository: PixRepository,
    val service: KeyService
) : KeymanagergrpcServiceGrpc.KeymanagergrpcServiceImplBase() {

    @ErrorHandler
    override fun cria(request: CreateKeyRequest, responseObserver: StreamObserver<CreateKeyResponse>) {
        val keyRequest = request.toModel()
        val registra = service.registra(keyRequest)

        responseObserver.onNext(registra)
        responseObserver.onCompleted()
    }
}