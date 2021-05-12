package br.com.zup.edu

import br.com.zup.edu.dto.CreatePixKeyRequest
import br.com.zup.edu.dto.CreatePixKeyResponse
import br.com.zup.edu.dto.KeyTypeToValidate
import br.com.zup.edu.dto.NovaChavePix
import br.com.zup.edu.handler.ClienteNaoEncontradoException
import br.com.zup.edu.handler.PixDuplicadoException
import br.com.zup.edu.httpclient.BacenClient
import br.com.zup.edu.httpclient.ERPClient
import br.com.zup.edu.repository.PixRepository
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.validation.Validated
import java.lang.RuntimeException
import javax.inject.Singleton
import javax.validation.Valid
import javax.validation.Validator

@Singleton
@OpenClass
class KeyService(
    val repository: PixRepository,
    val clientERP: ERPClient,
    val clientBacen: BacenClient,
) {
    fun registra(@Valid request: NovaChavePix): CreateKeyResponse {
        var pixResponse: CreatePixKeyResponse? = null
        if(request.tipoChave != KeyTypeToValidate.RANDOM)
            pixResponse = clientBacen.getPix(request.chave!!)

        if (repository.existsByKey(request.chave) || pixResponse != null)
            throw PixDuplicadoException()

        val card = clientERP
            .getCard(request.clienteId!!, request.tipoDeConta!!.name) ?: throw ClienteNaoEncontradoException()

        val pixRequest = card.let {
            CreatePixKeyRequest.build(request, it)
        }

        val generatePixKey = clientBacen.generatePixKey(pixRequest)
        val pix = repository.save(generatePixKey.toPixModel(request.clienteId!!))
        return CreateKeyResponse
            .newBuilder()
            .setIdPix(pix.id)
            .build()
    }
}