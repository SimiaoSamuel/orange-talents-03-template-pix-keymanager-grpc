package br.com.zup.edu.scheduled

import br.com.zup.edu.AccountType
import br.com.zup.edu.BacenManager
import br.com.zup.edu.KeyType
import br.com.zup.edu.dto.*
import br.com.zup.edu.httpclient.BacenClient
import br.com.zup.edu.model.Pix
import br.com.zup.edu.repository.PixRepository
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.time.LocalDateTime
import java.util.concurrent.CountDownLatch
import javax.inject.Inject


@MicronautTest(transactional = false)
class ScheduledTest(
) {
    @Inject
    lateinit var pixRepository: PixRepository

    @Inject
    lateinit var bacenClient: BacenClient

    @Inject
    lateinit var bacenManager: BacenManager

    val simulaPixInBacen = PixKeysListResponse(
        listOf(
            CreatePixKeyResponse(
                KeyType.CPF, "40028822200",
                BankAccount("", "", "", AccountBank.CACC),
                Owner(PersonType.NATURAL_PERSON, "", "81135157104"),
                LocalDateTime.now()
            )
        )
    )

    @BeforeEach
    fun setUp() {
        Mockito.`when`(bacenClient.listAllPix()).thenReturn(simulaPixInBacen)
        pixRepository.deleteAll()
    }

    @Test
    fun `adiciona pix no banco se ele existir no bacen`() {
        bacenManager.syncPix()
        Assertions.assertEquals(1L,pixRepository.count())
        Assertions.assertFalse(pixRepository.findAll().isEmpty())
    }

    @Test
    fun `nao adiciona pix no banco de dados se ele existe em ambos`() {
        pixRepository.save(
            Pix(
                keyType = KeyType.CPF,
                key = "40028822200",
                accountType = AccountType.CONTA_CORRENTE, owner = "81135157104"
            )
        )
        bacenManager.syncPix()
        Assertions.assertEquals(1L,pixRepository.count())
    }

    @Test
    fun `deleta pix do banco se ele nao existir no bacen`() {
        pixRepository.save(
            Pix(
                keyType = KeyType.CPF,
                key = "48710110110",
                accountType = AccountType.CONTA_CORRENTE, owner = "86135457004"
            )
        )
        bacenManager.syncPixBalancer()
        Assertions.assertTrue(pixRepository.findAll().isEmpty())
    }

    @Test
    fun `nao deleta pix do banco se ele existir no bacen`() {
        val copy = simulaPixInBacen.pixKeys.toMutableList()
        copy.add(
            CreatePixKeyResponse(
                KeyType.CPF, "48710110110",
                BankAccount("", "", "", AccountBank.CACC),
                Owner(PersonType.NATURAL_PERSON, "", "86135457004"),
                LocalDateTime.now()
            )
        )
        pixRepository.save(
            Pix(
                keyType = KeyType.CPF,
                key = "48710110110",
                accountType = AccountType.CONTA_CORRENTE, owner = "86135457004"
            )
        )

        Mockito.`when`(bacenClient.listAllPix()).thenReturn(PixKeysListResponse(copy))

        bacenManager.syncPixBalancer()
        Assertions.assertFalse(pixRepository.findAll().isEmpty())
    }

    @MockBean(BacenClient::class)
    fun bcbClient(): BacenClient {
        return Mockito.mock(BacenClient::class.java)
    }
}