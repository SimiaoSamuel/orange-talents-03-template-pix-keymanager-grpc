package br.com.zup.edu

import br.com.zup.edu.dto.*
import br.com.zup.edu.handler.ClienteNaoEncontradoException
import br.com.zup.edu.handler.DeletePixOwnerException
import br.com.zup.edu.handler.PixDuplicadoException
import br.com.zup.edu.handler.PixNaoEncontradoException
import br.com.zup.edu.httpclient.BacenClient
import br.com.zup.edu.httpclient.ERPClient
import br.com.zup.edu.repository.PixRepository
import javax.inject.Singleton
import javax.validation.Valid

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

        if(repository.existsByOwner(card.titular.cpf)) throw PixDuplicadoException()

        val pixRequest = card.let {
            CreatePixKeyRequest.build(request, it)
        }

        val generatePixKey = clientBacen.generatePixKey(pixRequest)
        val pix = repository.save(generatePixKey.toPixModel())
        return CreateKeyResponse
            .newBuilder()
            .setIdPix(pix.id)
            .build()
    }

    fun deleta(@Valid request: DeletaPixRequestValid){

        val pixModel = repository.findById(request.pixId!!).orElseThrow{
            throw PixNaoEncontradoException()
        }

        val card = clientERP.getCard(request.clientId!!, pixModel.accountType.name) ?: throw ClienteNaoEncontradoException()
        val participant = card.instituicao.ispb

        if(pixModel.owner != card.titular.cpf) throw DeletePixOwnerException()

        clientBacen.deletePix(pixModel.key, DeletePixKeyRequest(pixModel.key,participant))
        repository.delete(pixModel)
    }
}