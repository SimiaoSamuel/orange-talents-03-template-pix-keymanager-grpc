package br.com.zup.edu.dto

import br.com.zup.edu.DeleteKeyRequest
import br.com.zup.edu.OpenClass
import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected
import io.micronaut.core.annotation.NonNull
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank

@OpenClass
@Introspected
data class DeletaPixRequestValid(
    @field:NonNull
    val pixId: Long?,
    @field:NotBlank
    val clientId: String?
)

@OpenClass
data class DeletePixKeyResponse(
    @JsonProperty
    val key: String,
    @JsonProperty
    val participant: String,
    @JsonProperty
    val deletedAt: LocalDateTime
)

fun DeleteKeyRequest.toDto(): DeletaPixRequestValid{
    return DeletaPixRequestValid(pixId = this.idPix,clientId = this.idCliente)
}