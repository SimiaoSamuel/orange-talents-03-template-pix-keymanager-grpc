package br.com.zup.edu.dto

import br.com.zup.edu.ListKeyRequest
import br.com.zup.edu.OpenClass
import io.micronaut.core.annotation.Introspected
import org.jetbrains.annotations.NotNull
import javax.validation.constraints.NotBlank

@OpenClass
@Introspected
data class ListPixRequest(
    @field:NotBlank
    val idCliente: String?
)

fun ListKeyRequest.toDto(): ListPixRequest {
    return ListPixRequest(this.idCliente)
}