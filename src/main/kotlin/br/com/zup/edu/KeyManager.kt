package br.com.zup.edu

import br.com.zup.edu.dto.toDto
import br.com.zup.edu.dto.toPixChave
import br.com.zup.edu.dto.toPixId
import br.com.zup.edu.handler.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@OpenClass
class KeyManager(
    @Inject private val service: KeyService
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

    @ErrorHandler
    override fun busca(request: SearchKeyRequest, responseObserver: StreamObserver<SearchKeyResponse>) {
        val response = chooseMethodToSearch(request)

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }

    @ErrorHandler
    override fun lista(request: ListKeyRequest, responseObserver: StreamObserver<ListKeyResponse>) {
        val toDto = request.toDto()
        val listPix = service.listPix(toDto)

        responseObserver.onNext(listPix)
        responseObserver.onCompleted()
    }

    private fun chooseMethodToSearch(value: SearchKeyRequest): SearchKeyResponse {
        return if(value.hasPixCliente()) service.buscaPix(value.toPixId())
        else service.buscaPix(value.toPixChave())
    }
}