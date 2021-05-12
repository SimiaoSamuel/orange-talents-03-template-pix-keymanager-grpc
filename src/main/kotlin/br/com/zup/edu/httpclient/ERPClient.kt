package br.com.zup.edu.httpclient

import br.com.zup.edu.dto.AccountDetails
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client
interface ERPClient {
    @Get("http://localhost:9091/api/v1/clientes/{id}/contas")
    fun getCard(@PathVariable id: String, @QueryValue tipo: String): AccountDetails?
}