package br.com.zup.edu.enum

import br.com.zup.edu.AccountType
import br.com.zup.edu.dto.AccountBank
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class AccountBankTest {

    @Test
    fun `testa account bank poupanca`(){
        val accountBank = AccountBank.toBankType(AccountType.CONTA_POUPANCA)

        Assertions.assertNotNull(accountBank)
        Assertions.assertEquals(AccountBank.SVGS, accountBank)
    }

    @Test
    fun `testa account bank corrente`(){
        val accountBank = AccountBank.toBankType(AccountType.CONTA_CORRENTE)

        Assertions.assertNotNull(accountBank)
        Assertions.assertEquals(AccountBank.CACC, accountBank)
    }
}