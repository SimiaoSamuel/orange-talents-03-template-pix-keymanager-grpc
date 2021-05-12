package br.com.zup.edu.dto

import br.com.zup.edu.AccountType
import br.com.zup.edu.KeyType
import br.com.zup.edu.OpenClass
import br.com.zup.edu.model.Pix
import br.com.zup.edu.validation.ValidKey
import com.fasterxml.jackson.annotation.JsonProperty
import java.lang.RuntimeException
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@OpenClass
@ValidKey
data class CreatePixKeyRequest(
    @JsonProperty
    @field:NotNull
    val keyType: KeyTypeToValidate,
    @field:NotBlank
    @JsonProperty val key: String,
    @JsonProperty val bankAccount: BankAccount,
    @JsonProperty val owner: Owner
) {
    companion object {
        fun build(keyRequest: NovaChavePix, account: AccountDetails): CreatePixKeyRequest {

            val owner = Owner(
                type = PersonType.NATURAL_PERSON,
                name = account.titular.nome, taxIdNumber = account.titular.cpf
            )

            val bankAccount = BankAccount(
                participant = account.instituicao.ispb, branch = account.agencia,
                accountNumber = account.numero, accountType = AccountBank.toBankType(account.tipo)
            )

            return CreatePixKeyRequest(keyRequest.tipoChave!!, keyRequest.chave!!, bankAccount, owner)
        }
    }
}

@OpenClass
data class CreatePixKeyResponse(
    @JsonProperty val keyType: KeyType,
    @JsonProperty val key: String,
    @JsonProperty val bankAccount: BankAccount,
    @JsonProperty val owner: Owner,
    @JsonProperty val createdAt: LocalDateTime
){
    fun toPixModel(clienteId: String): Pix{
        return Pix(key = key, keyType = keyType, owner = clienteId)
    }
}

@OpenClass
data class BankAccount(
    val participant: String,
    val branch: String,
    val accountNumber: String,
    val accountType: AccountBank
)

@OpenClass
data class Owner(
    val type: PersonType,
    val name: String,
    val taxIdNumber: String
)

@OpenClass
enum class AccountBank {
    CACC, SVGS;

    companion object {
        fun toBankType(type: AccountType): AccountBank {
            return when (type) {
                AccountType.CONTA_CORRENTE -> CACC
                AccountType.CONTA_POUPANCA -> SVGS
                else -> throw RuntimeException()
            }
        }
    }
}

@OpenClass
enum class PersonType {
    NATURAL_PERSON, LEGAL_PERSON
}