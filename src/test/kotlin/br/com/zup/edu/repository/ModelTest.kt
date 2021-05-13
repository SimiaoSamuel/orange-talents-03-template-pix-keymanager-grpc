package br.com.zup.edu.repository

import br.com.zup.edu.AccountType
import br.com.zup.edu.KeyType
import br.com.zup.edu.model.Pix
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@MicronautTest
class ModelTest(private val repository: PixRepository) {

    @Test
    fun salvando_pix_random() {
        val pixSalvo = repository.save(
            Pix(
                keyType = KeyType.EMAIL,
                key = "xpto@gmail.com",
                accountType = AccountType.CONTA_POUPANCA,
                owner = "xpto"
            )
        )

        Assertions.assertEquals(KeyType.EMAIL,pixSalvo.keyType)
    }
}