package br.com.zup.edu.httpclient

import br.com.zup.edu.dto.*
import br.com.zup.edu.handler.ErrorHandler
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
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

    @Delete(
        "http://localhost:8082/api/v1/pix/keys/{key}", consumes = [MediaType.APPLICATION_XML],
        produces = [MediaType.APPLICATION_XML]
    )
    fun deletePix(@PathVariable key: String, @Body request: DeletePixKeyRequest): DeletePixKeyResponse?

    @Get("http://localhost:8082/api/v1/pix/keys", produces = [MediaType.APPLICATION_XML])
    fun listAllPix(): PixKeysListResponse?
}