package br.com.zup.edu.pix

import br.com.zup.edu.*
import br.com.zup.edu.dto.*
import br.com.zup.edu.dto.Titular
import br.com.zup.edu.httpclient.BacenClient
import br.com.zup.edu.httpclient.ERPClient
import br.com.zup.edu.repository.PixRepository
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mockito
import java.time.LocalDateTime
import java.util.stream.Stream
import javax.inject.Inject

@MicronautTest(transactional = false)
@OpenClass
class RegistraChaveEndpointTest(
    val repository: PixRepository,
    val grpcClient: KeymanagergrpcServiceGrpc.KeymanagergrpcServiceBlockingStub
) {

    @Inject
    lateinit var bcbClient: BacenClient

    @Inject
    lateinit var erpClient: ERPClient

    private val requestObjectValid = CreateKeyRequest.newBuilder()
        .setChave("48791010870")
        .setTipoChave(KeyType.CPF)
        .setIdCliente("ae93a61c-0642-43b3-bb8e-a17072295955")
        .setTipoConta(AccountType.CONTA_POUPANCA)
        .build()

    @BeforeEach
    fun setup() {
        Mockito.`when`(erpClient.getCard(requestObjectValid.idCliente, requestObjectValid.tipoConta.name))
            .thenReturn(accountDetailsDto())

        Mockito.`when`(bcbClient.generatePixKey(any(CreatePixKeyRequest::class.java))).thenReturn(pixResponseTest())

        repository.deleteAll()
    }

    @Test
    fun `deve registrar chave pix`() {
        val response = grpcClient.cria(requestObjectValid)

        with(response){
            Assertions.assertNotNull(this)
            Assertions.assertEquals(1L,this.idPix)
        }
    }

    @ParameterizedTest
    @MethodSource("dadosInvalidos")
    fun `nao deve salvar se dados estiverem incorretos`(
        chave: String, type: KeyType?, clienteId: String, conta: AccountType
    ){
        val requestObject = CreateKeyRequest.newBuilder()
            .setChave(chave)
            .setIdCliente(clienteId)
            .setTipoConta(conta)
            .build()

        val assert = assertThrows<StatusRuntimeException> {
            grpcClient.cria(requestObject)
        }
        with(assert){
            Assertions.assertNotNull(this)
            Assertions.assertEquals(Status.INVALID_ARGUMENT.code, this.status.code)
        }
    }

    @Test
    fun `deve lancar excecao se chave ja estiver cadastrada no banco`(){
        grpcClient.cria(requestObjectValid)

        val assert = assertThrows<StatusRuntimeException> {
            grpcClient.cria(CreateKeyRequest.newBuilder()
                .setChave("48791010870")
                .setTipoChave(KeyType.CPF)
                .setIdCliente("5260263c-a3c1-4727-ae32-3bdb2538841b")
                .setTipoConta(AccountType.CONTA_POUPANCA)
                .build()
            )
        }

        with(assert){
            Assertions.assertNotNull(this)
            Assertions.assertEquals(Status.ALREADY_EXISTS.code, this.status.code)
        }
    }

    @Test
    fun `deve lancar excecao se id cliente nao existir no sistema erp externo`(){
        val requestInvalidId = CreateKeyRequest.newBuilder()
            .setChave("48791010870")
            .setTipoChave(KeyType.CPF)
            .setIdCliente("id cliente não valido")
            .setTipoConta(AccountType.CONTA_POUPANCA)
            .build()

        val assert = assertThrows<StatusRuntimeException> {
            grpcClient.cria(requestInvalidId)
        }
        with(assert){
            Assertions.assertNotNull(this)
            Assertions.assertEquals(Status.NOT_FOUND.code, this.status.code)
        }
    }

    companion object {
        @JvmStatic
        fun dadosValidos() = Stream.of(
            Arguments.of("48791010870",KeyType.CPF,"ae93a61c-0642-43b3-bb8e-a17072295955", AccountType.CONTA_POUPANCA),
            Arguments.of("xpto@gmail.com",KeyType.EMAIL,"c56dfef4-7901-44fb-84e2-a2cefb157890", AccountType.CONTA_CORRENTE),
            Arguments.of("+5585988714077",KeyType.PHONE,"5260263c-a3c1-4727-ae32-3bdb2538841b", AccountType.CONTA_CORRENTE),
            Arguments.of("",KeyType.RANDOM,"0d1bb194-3c52-4e67-8c35-a93c0af9284f", AccountType.CONTA_CORRENTE),
        )
        @JvmStatic
        fun dadosInvalidos() = Stream.of(
            Arguments.of("xxx910108xx",KeyType.CPF,"", AccountType.CONTA_POUPANCA),
            Arguments.of("xpto@gmail.comeu",KeyType.EMAIL,"", AccountType.CONTA_CORRENTE),
            Arguments.of("+55888714077",KeyType.PHONE,"", AccountType.CONTA_CORRENTE),
            Arguments.of("",null,"", AccountType.CONTA_CORRENTE),
        )
    }

    private fun <T> any(type: Class<T>): T = Mockito.any(type)

    fun pixResponseTest(): CreatePixKeyResponse {
        return CreatePixKeyResponse(
            KeyType.CPF,
            "48791010870",
            BankAccount("","","",AccountBank.SVGS),
            Owner(PersonType.NATURAL_PERSON,"",""),
            LocalDateTime.now()
        )
    }

    fun accountDetailsDto(): AccountDetails {
        return AccountDetails(
            AccountType.CONTA_POUPANCA,
            Institution("", ""),
            "agencia",
            "numero",
            Titular("","","")
        )
    }

    @MockBean(BacenClient::class)
    fun bcbClient(): BacenClient {
        return Mockito.mock(BacenClient::class.java)
    }

    @MockBean(ERPClient::class)
    fun erpClient(): ERPClient {
        return Mockito.mock(ERPClient::class.java)
    }
}