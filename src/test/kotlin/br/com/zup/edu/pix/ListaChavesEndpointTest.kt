package br.com.zup.edu.pix

import br.com.zup.edu.AccountType
import br.com.zup.edu.KeyType
import br.com.zup.edu.KeymanagergrpcServiceGrpc
import br.com.zup.edu.ListKeyRequest
import br.com.zup.edu.dto.*
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
class ListaChavesEndpointTest {
    @Inject
    lateinit var erpClient: ERPClient

    @Inject
    lateinit var repository: PixRepository

    @Inject
    lateinit var clientGrpc: KeymanagergrpcServiceGrpc.KeymanagergrpcServiceBlockingStub

    val cliente = Cliente(
        id = "c56dfef4-7901-44fb-84e2-a2cefb157890",
        nome = "ponte",
        cpf = "02467781054",
        instituicao = Institution("", "")
    )

    val clienteSemPix = Cliente(
        id = "5260263c-a3c1-4727-ae32-3bdb2538841b",
        nome = "yuri",
        cpf = "86135457004",
        instituicao = Institution("", "")
    )

    @BeforeEach
    fun setUp() {
        repository.save(
            Pix(
                keyType = KeyType.EMAIL,
                key = "ponte@gmail.com",
                tipoConta = AccountType.CONTA_POUPANCA,
                owner = Owner(PersonType.NATURAL_PERSON, "ponte", "02467781054"),
                conta = BankAccount("", "", "", AccountBank.SVGS),
                createdAt = LocalDateTime.now()
            )
        )
        repository.save(
            Pix(
                keyType = KeyType.CPF,
                key = "02467781054",
                tipoConta = AccountType.CONTA_POUPANCA,
                owner = Owner(PersonType.NATURAL_PERSON, "ponte", "02467781054"),
                conta = BankAccount("", "", "", AccountBank.SVGS),
                createdAt = LocalDateTime.now()
            )
        )
        Mockito.`when`(erpClient.getCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")).thenReturn(cliente)
        Mockito.`when`(erpClient.getCliente("5260263c-a3c1-4727-ae32-3bdb2538841b")).thenReturn(clienteSemPix)
        Mockito.`when`(erpClient.getCliente("invalido")).thenReturn(null)
    }

    @AfterEach
    fun tearDown(){
        repository.deleteAll()
    }

    @Test
    fun `testando se retorna lista de chaves do cliente valido`() {
        val lista = clientGrpc.lista(
            ListKeyRequest
                .newBuilder()
                .setIdCliente(cliente.id)
                .build()
        )

        Assertions.assertEquals(2,lista.keyList.size)
    }

    @Test
    fun `testando se retorna lista vazia de cliente sem pix cadastrado`() {
        val lista = clientGrpc.lista(
            ListKeyRequest
                .newBuilder()
                .setIdCliente(clienteSemPix.id)
                .build()
        )

        Assertions.assertEquals(0,lista.keyList.size)
    }

    @Test
    fun `testando busca de chaves com cliente invalido`() {

        val assertThrows = assertThrows<StatusRuntimeException> {
            clientGrpc.lista(
                ListKeyRequest
                    .newBuilder()
                    .setIdCliente("invalido")
                    .build()
            )
        }

        Assertions.assertEquals(Status.NOT_FOUND.code, assertThrows.status.code)
    }

    @MockBean(ERPClient::class)
    fun erpClient(): ERPClient {
        return Mockito.mock(ERPClient::class.java)
    }
}