package br.com.zup.edu

import br.com.zup.edu.dto.*
import br.com.zup.edu.handler.ClienteNaoEncontradoException
import br.com.zup.edu.handler.PixOwnerException
import br.com.zup.edu.handler.PixDuplicadoException
import br.com.zup.edu.handler.PixNaoEncontradoException
import br.com.zup.edu.httpclient.BacenClient
import br.com.zup.edu.httpclient.ERPClient
import br.com.zup.edu.model.Pix
import br.com.zup.edu.repository.PixRepository
import com.google.protobuf.Timestamp
import java.time.LocalDateTime
import javax.inject.Singleton
import javax.validation.Valid
import java.time.ZoneOffset


@Singleton
@OpenClass
class KeyService(
    val repository: PixRepository,
    val clientERP: ERPClient,
    val clientBacen: BacenClient,
) {

    fun registra(@Valid request: NovaChavePix): CreateKeyResponse {
        var pixResponse: CreatePixKeyResponse? = null
        if (request.tipoChave != KeyTypeToValidate.RANDOM)
            pixResponse = clientBacen.getPix(request.chave!!)

        if (repository.existsByKey(request.chave) || pixResponse != null)
            throw PixDuplicadoException()

        val card = clientERP
            .getCard(request.clienteId!!, request.tipoDeConta!!.name) ?: throw ClienteNaoEncontradoException()

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

    fun deleta(@Valid request: DeletaPixRequestValid) {

        val pixModel = repository.findById(request.pixId!!).orElseThrow {
            throw PixNaoEncontradoException()
        }

        val card =
            clientERP.getCard(request.clientId!!, pixModel.tipoConta.name) ?: throw ClienteNaoEncontradoException()
        val participant = card.instituicao.ispb

        if (pixModel.owner.taxIdNumber != card.titular.cpf) throw PixOwnerException()

        clientBacen.deletePix(pixModel.key, DeletePixKeyRequest(pixModel.key, participant))
        repository.delete(pixModel)
    }

    fun buscaPix(@Valid request: BuscaPixPorChave): SearchKeyResponse {
        val pixResponse: Pix
        val pixDbOptional = repository.findByKey(request.pixChave!!)

        pixResponse = if (pixDbOptional != null)
            pixDbOptional
        else {
            val pixBacen = clientBacen.getPix(request.pixChave!!) ?: throw PixNaoEncontradoException()
            pixBacen.toPixModel()
        }

        val pixInfo = buildResponse(pixResponse)

        return SearchKeyResponse.newBuilder()
            .setPix(pixInfo)
            .setDataCriacao(timestamp(pixResponse.createdAt))
            .build()
    }

    fun buscaPix(@Valid request: BuscaPixPorId): SearchKeyResponse {
        val pixResponse = repository.findById(request.idPix!!).orElseThrow { PixNaoEncontradoException() }

        val cliente = clientERP.getCliente(request.idCliente!!) ?: throw ClienteNaoEncontradoException()

        if(cliente.cpf != pixResponse.owner.taxIdNumber) throw PixOwnerException()

        val pixInfo = buildResponse(pixResponse)

        return SearchKeyResponse.newBuilder()
            .setPix(pixInfo)
            .setIdCliente(request.idCliente)
            .setIdPix(pixResponse.id)
            .setDataCriacao(timestamp(pixResponse.createdAt))
            .build()
    }

    fun listPix(@Valid request: ListPixRequest): ListKeyResponse {
        val cliente = clientERP.getCliente(request.idCliente!!) ?: throw ClienteNaoEncontradoException()

        val pix = repository.findByOwnerTaxIdNumber(cliente.cpf)
        val map = pix.map {
            KeyEntityResponse.newBuilder()
                .setChave(it.key)
                .setDataCriacao(timestamp(it.createdAt))
                .setTipoConta(it.tipoConta)
                .setTipoChave(it.keyType)
                .setIdPix(it.id)
                .build()
        }

        return ListKeyResponse.newBuilder()
            .addAllKey(map)
            .setIdCliente(request.idCliente)
            .build()
    }

    private fun buildResponse(pixResponse: Pix)
            : PixKey {
        val bankAccount = pixResponse.conta
        val accountType = bankAccount.accountType.toAccountType()
        val owner = pixResponse.owner

        val titular = Titular.newBuilder()
            .setCpf(owner.taxIdNumber)
            .setNome(owner.name)
            .build()

        val conta = Conta.newBuilder()
            .setNumeroConta(bankAccount.accountNumber)
            .setTipoConta(accountType)
            .setAgencia(bankAccount.branch)
            .setIntituicao(bankAccount.participant)
            .build()

        val pix = PixKey.newBuilder()
            .setChave(pixResponse.key)
            .setNome(titular)
            .setConta(conta)
            .build()

        return pix
    }

    private fun timestamp(localDateTime: LocalDateTime): Timestamp? {
        val instant = localDateTime.toInstant(ZoneOffset.UTC)
        return Timestamp.newBuilder()
            .setSeconds(instant.epochSecond)
            .setNanos(instant.nano)
            .build()
    }
}