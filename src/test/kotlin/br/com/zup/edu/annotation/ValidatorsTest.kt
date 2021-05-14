package br.com.zup.edu.annotation

import br.com.zup.edu.AccountType
import br.com.zup.edu.KeyType
import br.com.zup.edu.dto.*
import br.com.zup.edu.model.Pix
import br.com.zup.edu.repository.PixRepository
import br.com.zup.edu.validation.KeyValidator
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import javax.validation.Validator

@MicronautTest
class ValidatorsTest(
    private val validator: Validator,
    private val repository: PixRepository
) {

    @Test
    fun `testando `() {
        val novaChavePix = NovaChavePix(null, null, "asoihdfsdh", AccountType.CONTA_CORRENTE)
        val validate = validator.validate(novaChavePix)

        Assertions.assertTrue(validate.isNotEmpty())
    }
}