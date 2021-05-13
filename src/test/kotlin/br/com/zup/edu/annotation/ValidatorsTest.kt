package br.com.zup.edu.annotation

import br.com.zup.edu.AccountType
import br.com.zup.edu.KeyType
import br.com.zup.edu.dto.KeyTypeToValidate
import br.com.zup.edu.dto.NovaChavePix
import br.com.zup.edu.model.Pix
import br.com.zup.edu.repository.PixRepository
import br.com.zup.edu.validation.KeyValidator
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
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

    @Test
    fun `testando se existe cliente no banco pix validator`() {
        repository.save(Pix(
            keyType = KeyType.CPF,
            key = "18191010171",
            accountType = AccountType.CONTA_POUPANCA,
            owner = "cliente"
        ))
        val novaChavePix = NovaChavePix("cliente", KeyTypeToValidate.CPF, "41110891010", AccountType.CONTA_CORRENTE)
        val validate = validator.validate(novaChavePix)
        Assertions.assertTrue(validate.isNotEmpty())
    }
}