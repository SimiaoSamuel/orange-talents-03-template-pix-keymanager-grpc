package br.com.zup.edu.annotation

import br.com.zup.edu.AccountType
import br.com.zup.edu.dto.NovaChavePix
import br.com.zup.edu.validation.KeyValidator
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import javax.validation.Validator

@MicronautTest
class NovaChavePixTest(
    private val validator: Validator,
) {

    @Test
    fun `testando `() {
        val novaChavePix = NovaChavePix("slamdpopsad", null, "asoihdfsdh", AccountType.CONTA_CORRENTE)
        val validate = validator.validate(novaChavePix)

        Assertions.assertTrue(validate.isNotEmpty())
    }
}