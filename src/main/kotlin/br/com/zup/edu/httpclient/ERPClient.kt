package br.com.zup.edu.httpclient

import br.com.zup.edu.dto.AccountDetails
import br.com.zup.edu.dto.Cliente
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client("\${pix.itau-url}")
interface ERPClient {
    @Get("/api/v1/clientes/{id}/contas")
    fun getCard(@PathVariable id: String, @QueryValue tipo: String): AccountDetails?

    @Get("/api/v1/clientes/{id}")
    fun getCliente(@PathVariable id: String): Cliente?
}