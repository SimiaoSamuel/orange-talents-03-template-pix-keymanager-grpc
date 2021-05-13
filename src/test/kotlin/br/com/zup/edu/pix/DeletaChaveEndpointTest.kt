package br.com.zup.edu.pix

import br.com.zup.edu.*
import br.com.zup.edu.dto.*
import br.com.zup.edu.httpclient.BacenClient
import br.com.zup.edu.httpclient.ERPClient
import br.com.zup.edu.model.Pix
import br.com.zup.edu.repository.PixRepository
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.*
import org.mockito.Mockito
import java.time.LocalDateTime
import javax.inject.Inject

@MicronautTest(transactional = false)
@OpenClass
class DeletaChaveEndpointTest(
    val repository: PixRepository,
    val grpcClient: KeymanagergrpcServiceGrpc.KeymanagergrpcServiceBlockingStub
) {
    @Inject
    lateinit var bcbClient: BacenClient

    @Inject
    lateinit var erpClient: ERPClient

    val validDeleteRequest = DeleteKeyRequest.newBuilder()
        .setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
        .setIdPix(1)
        .build()

    @BeforeEach
    fun setup() {
        Mockito.`when`(erpClient.getCard("xd", AccountType.CONTA_POUPANCA.name))
            .thenReturn(null)

        Mockito.`when`(bcbClient.deletePix(Mockito.anyString(), any(DeletePixKeyRequest::class.java)))
            .thenReturn(DeletePixKeyResponse("", "", LocalDateTime.now()))

        Mockito.`when`(erpClient.getCard("c56dfef4-7901-44fb-84e2-a2cefb157890", AccountType.CONTA_POUPANCA.name))
            .thenReturn(
                AccountDetails(
                    AccountType.CONTA_POUPANCA,
                    Institution("", ""),
                    "agencia",
                    "numero",
                    Titular("", "", "02467781054")
                )
            )
    }

    @AfterEach
    fun tearDown() {
        repository.deleteAll()
    }

    private fun <T> any(type: Class<T>): T = Mockito.any(type)

    @Test
    fun `valida dados do objeto da requisicao`() {
        val request = DeleteKeyRequest.newBuilder().build()
        val returnThrow = assertThrows<StatusRuntimeException> {
            grpcClient.deleta(request)
        }

        Assertions.assertNotNull(returnThrow)
        Assertions.assertEquals(Status.INVALID_ARGUMENT.code, returnThrow.status.code)
    }

    @Test
    fun `valida se pix existe no banco`() {
        val returnThrow = assertThrows<StatusRuntimeException> {
            grpcClient.deleta(validDeleteRequest)
        }

        Assertions.assertNotNull(returnThrow)
        Assertions.assertEquals(Status.NOT_FOUND.code, returnThrow.status.code)
    }

    @Test
    fun `testa se dados passados correspondem a um cliente valido no erp`() {
        repository.save(
            Pix(
                keyType = KeyType.CPF,
                key = "18191010171",
                accountType = AccountType.CONTA_POUPANCA,
                owner = "xpto"
            )
        )

        val returnThrow = assertThrows<StatusRuntimeException> {
            grpcClient.deleta(
                DeleteKeyRequest.newBuilder()
                    .setIdCliente("xd")
                    .setIdPix(1L).build()
            )
        }

        Assertions.assertNotNull(returnThrow)
        Assertions.assertEquals(Status.NOT_FOUND.code, returnThrow.status.code)
    }

    @Test
    fun `testa se o dono daquele pix que esta tentando remove-lo`() {
        val pixSalvo = repository.save(
            Pix(
                keyType = KeyType.CPF,
                key = "18191010171",
                accountType = AccountType.CONTA_POUPANCA,
                owner = "xpto"
            )
        )

        val returnThrow = assertThrows<StatusRuntimeException> {
            grpcClient.deleta(
                DeleteKeyRequest.newBuilder()
                    .setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
                    .setIdPix(pixSalvo.id).build()
            )
        }

        Assertions.assertNotNull(returnThrow)
        Assertions.assertEquals(Status.PERMISSION_DENIED.code, returnThrow.status.code)
    }

    @Test
    fun `deve remover um pix do sistema`() {
        val pixSalvo = repository.save(
            Pix(
                keyType = KeyType.CPF,
                key = "18191010171",
                accountType = AccountType.CONTA_POUPANCA,
                owner = "02467781054"
            )
        )

        grpcClient.deleta(
            DeleteKeyRequest.newBuilder()
                .setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
                .setIdPix(pixSalvo.id).build()
        )

        val existsByKey = repository.existsByKey("18191010171")

        Assertions.assertFalse(existsByKey)
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