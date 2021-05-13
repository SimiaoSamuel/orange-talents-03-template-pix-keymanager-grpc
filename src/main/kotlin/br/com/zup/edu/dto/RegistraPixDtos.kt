package br.com.zup.edu.dto

import br.com.zup.edu.AccountType
import br.com.zup.edu.CreateKeyRequest
import br.com.zup.edu.OpenClass
import br.com.zup.edu.validation.ExistsPixForThisClient
import br.com.zup.edu.validation.ValidKey
import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected
import org.apache.commons.validator.routines.EmailValidator
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@OpenClass
@Introspected
data class AccountDetails(
    @field:NotNull
    @JsonProperty
    val tipo: AccountType,
    @field:NotNull
    @JsonProperty
    val instituicao: Institution,
    @field:NotBlank
    @JsonProperty
    val agencia: String,
    @field:NotBlank
    @JsonProperty
    val numero: String,
    @field:NotNull
    @JsonProperty
    val titular: Titular
)

@OpenClass
data class Institution(
    @JsonProperty
    val nome: String,
    @JsonProperty
    val ispb: String
)

@OpenClass
data class Titular(
    @JsonProperty
    val id: String,
    @JsonProperty
    val nome: String,
    @JsonProperty
    val cpf: String
)

@OpenClass
@Introspected
@ValidKey
data class NovaChavePix (
    @field:NotBlank
    @field:ExistsPixForThisClient
    @JsonProperty
    val clienteId: String?,
    @field:NotNull
    @JsonProperty
    val tipoChave: KeyTypeToValidate?,
    @JsonProperty
    val chave: String?,
    @field:NotNull
    @JsonProperty
    val tipoDeConta: AccountType?
)

fun CreateKeyRequest.toDto(): NovaChavePix{
    return NovaChavePix(this.idCliente,KeyTypeToValidate.valueOf(this.tipoChave.name),this.chave,this.tipoConta)
}

@OpenClass
enum class KeyTypeToValidate{
    CPF{
        override fun validate(key: String?): Boolean {
           return !key.isNullOrBlank() && key.matches("^[0-9]{11}$".toRegex())
        }
    },
    PHONE{
        override fun validate(key: String?): Boolean {
            return !key.isNullOrBlank() && key.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())
        }
    },
    EMAIL{
        override fun validate(key: String?): Boolean {
            return !key.isNullOrBlank() && EmailValidator.getInstance().isValid(key)
        }
    },
    RANDOM{
        override fun validate(key: String?): Boolean {
            return key.isNullOrBlank()
        }
    };

    abstract fun validate(key: String?): Boolean
}

