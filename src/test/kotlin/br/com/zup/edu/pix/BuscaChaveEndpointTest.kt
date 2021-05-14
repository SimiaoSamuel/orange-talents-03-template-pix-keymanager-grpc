package br.com.zup.edu.pix

import br.com.zup.edu.AccountType
import br.com.zup.edu.KeyType
import br.com.zup.edu.KeymanagergrpcServiceGrpc
import br.com.zup.edu.SearchKeyRequest
import br.com.zup.edu.dto.*
import br.com.zup.edu.handler.PixNaoEncontradoException
import br.com.zup.edu.httpclient.BacenClient
import br.com.zup.edu.httpclient.ERPClient
import br.com.zup.edu.model.Pix
import br.com.zup.edu.repository.PixRepository
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.time.LocalDateTime
import javax.inject.Inject
import javax.transaction.Transactional

@MicronautTest(transactional = false)
class BuscaChaveEndpointTest {
    @Inject
    lateinit var pixRepository: PixRepository

    @Inject
    lateinit var bacenClient: BacenClient

    @Inject
    lateinit var erpClient: ERPClient

    @Inject
    lateinit var grpcClient: KeymanagergrpcServiceGrpc.KeymanagergrpcServiceBlockingStub


    val pix = CreatePixKeyResponse(
        KeyType.EMAIL,
        "xpto@gmail.com",
        BankAccount("", "", "", AccountBank.SVGS),
        Owner(PersonType.NATURAL_PERSON, "", ""),
        LocalDateTime.now()
    )

    val cliente = Cliente(
        id = "c56dfef4-7901-44fb-84e2-a2cefb157890",
        nome = "ponte",
        cpf = "02467781054",
        instituicao = Institution("", "")
    )

    val pixNoBancoDonoValido =
        Pix(
            keyType = KeyType.EMAIL,
            key = "ponte@gmail.com",
            tipoConta = AccountType.CONTA_POUPANCA,
            owner = Owner(PersonType.NATURAL_PERSON, "ponte", "02467781054"),
            conta = BankAccount("", "", "", AccountBank.SVGS),
            createdAt = LocalDateTime.now()
        )

    val pixNoBancoDonoInvalido =
        Pix(
            keyType = KeyType.CPF,
            key = "10110110110",
            tipoConta = AccountType.CONTA_POUPANCA,
            owner = Owner(PersonType.NATURAL_PERSON, "ponte", "xxxxxxxxx"),
            conta = BankAccount("", "", "", AccountBank.SVGS),
            createdAt = LocalDateTime.now()
        )


    @BeforeEach
    fun setUp() {
        Mockito.`when`(bacenClient.getPix("xpto@gmail.com")).thenReturn(pix)
        Mockito.`when`(bacenClient.getPix("invalida")).thenReturn(null)
        Mockito.`when`(erpClient.getCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")).thenReturn(cliente)
        Mockito.`when`(erpClient.getCliente("invalido")).thenReturn(null)
        pixRepository.deleteAll()
    }

    private fun <T> any(type: Class<T>): T = Mockito.any(type)

    @Test
    fun `se pesquisa for por chave pix valida retorna ela`() {
        val busca = grpcClient.busca(
            SearchKeyRequest.newBuilder()
                .setPixValue("xpto@gmail.com")
                .build()
        )

        Assertions.assertEquals("xpto@gmail.com", busca.pix.chave)
    }

    @Test
    fun `se pesquisa for por chave pix invalida retorna exception`() {
        val assertThrows = assertThrows<StatusRuntimeException> {
            grpcClient.busca(
                SearchKeyRequest.newBuilder()
                    .setPixValue("invalida")
                    .build()
            )
        }

        Assertions.assertEquals(Status.NOT_FOUND.code, assertThrows.status.code)
    }


    @Test
    fun `se pesquisa for por chave pix valida e existir no banco retorna ela`() {
        pixRepository.save(
            Pix(
                keyType = KeyType.CPF,
                key = "10110110110",
                tipoConta = AccountType.CONTA_POUPANCA,
                owner = Owner(PersonType.NATURAL_PERSON, "", ""),
                conta = BankAccount("", "", "", AccountBank.SVGS),
                createdAt = LocalDateTime.now()
            )
        )

        val busca = grpcClient.busca(
            SearchKeyRequest.newBuilder()
                .setPixValue("10110110110")
                .build()
        )

        Assertions.assertEquals("10110110110", busca.pix.chave)
        Assertions.assertEquals(KeyType.CPF.name, busca.pix.tipoChave.name)
    }

    @Test
    fun `se pesquisa for por cliente e pix id e valida retorna pix`() {
        val pixSalvo = pixRepository.save(pixNoBancoDonoValido)

        val response = grpcClient.busca(
            SearchKeyRequest.newBuilder()
                .setPixCliente(
                    SearchKeyRequest.SearchPixId.newBuilder()
                        .setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
                        .setIdPix(pixSalvo.id)
                        .build()
                )
                .build()
        )

        Assertions.assertEquals(pixSalvo.key, response.pix.chave)
    }

    @Test
    fun `se pesquisa for por cliente e pix id e pix nao existe`() {
        val assertThrows = assertThrows<StatusRuntimeException> {
            grpcClient.busca(
                SearchKeyRequest.newBuilder()
                    .setPixCliente(
                        SearchKeyRequest.SearchPixId.newBuilder()
                            .setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
                            .setIdPix(1L)
                            .build()
                    )
                    .build()
            )
        }

        Assertions.assertEquals(Status.NOT_FOUND.code,assertThrows.status.code)
    }

    @Test
    fun `se pesquisa for por cliente e pix id e cliente nao existe no erp`() {
        pixRepository.save(pixNoBancoDonoValido)

        val assertThrows = assertThrows<StatusRuntimeException> {
            grpcClient.busca(
                SearchKeyRequest.newBuilder()
                    .setPixCliente(
                        SearchKeyRequest.SearchPixId.newBuilder()
                            .setIdCliente("invalido")
                            .setIdPix(1L)
                            .build()
                    )
                    .build()
            )
        }

        Assertions.assertEquals(Status.NOT_FOUND.code,assertThrows.status.code)
    }

    @Test
    fun `se pesquisa for por cliente e pix id e cliente nao e dono do pix`() {
        pixRepository.save(pixNoBancoDonoInvalido)

        val assertThrows = assertThrows<StatusRuntimeException> {
            grpcClient.busca(
                SearchKeyRequest.newBuilder()
                    .setPixCliente(
                        SearchKeyRequest.SearchPixId.newBuilder()
                            .setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
                            .setIdPix(1L)
                            .build()
                    )
                    .build()
            )
        }

        Assertions.assertEquals(Status.PERMISSION_DENIED.code,assertThrows.status.code)
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