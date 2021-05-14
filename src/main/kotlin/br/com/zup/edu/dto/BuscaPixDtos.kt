package br.com.zup.edu.dto

import br.com.zup.edu.OpenClass
import br.com.zup.edu.SearchKeyRequest
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Introspected
@OpenClass
data class BuscaPixPorId(
    @field:NotBlank
    val idCliente: String?,
    @field:NotNull
    val idPix: Long?,
)

fun SearchKeyRequest.toPixId(): BuscaPixPorId {
    return BuscaPixPorId(this.pixCliente.idCliente, this.pixCliente.idPix)
}

@Introspected
@OpenClass
data class BuscaPixPorChave(
    @field:NotBlank
    val pixChave: String?,
)

fun SearchKeyRequest.toPixChave(): BuscaPixPorChave {
    return BuscaPixPorChave(this.pixValue)
}
