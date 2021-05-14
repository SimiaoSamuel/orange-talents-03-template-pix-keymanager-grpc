package br.com.zup.edu.repository

import br.com.zup.edu.AccountType
import br.com.zup.edu.KeyType
import br.com.zup.edu.dto.AccountBank
import br.com.zup.edu.dto.BankAccount
import br.com.zup.edu.dto.Owner
import br.com.zup.edu.dto.PersonType
import br.com.zup.edu.model.Pix
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@MicronautTest
class ModelTest(private val repository: PixRepository) {

    @Test
    fun salvando_pix_random() {
        val pixSalvo = repository.save(
            Pix(
                keyType = KeyType.EMAIL,
                key = "xpto@gmail.com",
                tipoConta = AccountType.CONTA_POUPANCA,
                conta = BankAccount("","","", AccountBank.SVGS),
                owner = Owner(PersonType.NATURAL_PERSON,"","xpto"),
                createdAt = LocalDateTime.now()
            )
        )

        Assertions.assertEquals(KeyType.EMAIL,pixSalvo.keyType)
    }
}