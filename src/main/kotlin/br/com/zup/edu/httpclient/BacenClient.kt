package br.com.zup.edu.httpclient

import br.com.zup.edu.dto.CreatePixKeyRequest
import br.com.zup.edu.dto.CreatePixKeyResponse
import br.com.zup.edu.handler.ErrorHandler
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client

@Client
interface BacenClient {
    @Post(
        "http://localhost:8082/api/v1/pix/keys", consumes = [MediaType.APPLICATION_XML],
        produces = [MediaType.APPLICATION_XML]
    )
    fun generatePixKey(@Body request: CreatePixKeyRequest): CreatePixKeyResponse

    @Get(
        "http://localhost:8082/api/v1/pix/keys/{key}", consumes = [MediaType.APPLICATION_XML],
        produces = [MediaType.APPLICATION_XML]
    )
    fun getPix(@PathVariable key: String): CreatePixKeyResponse?
}