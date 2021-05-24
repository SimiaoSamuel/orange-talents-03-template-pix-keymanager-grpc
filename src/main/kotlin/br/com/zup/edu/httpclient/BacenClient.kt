package br.com.zup.edu.httpclient

import br.com.zup.edu.dto.*
import br.com.zup.edu.handler.ErrorHandler
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client

@Client("\${pix.bcb-url}")
interface BacenClient {
    @Post(
        "/api/v1/pix/keys", consumes = [MediaType.APPLICATION_XML],
        produces = [MediaType.APPLICATION_XML]
    )
    fun generatePixKey(@Body request: CreatePixKeyRequest): CreatePixKeyResponse

    @Get(
        "/api/v1/pix/keys/{key}", consumes = [MediaType.APPLICATION_XML],
        produces = [MediaType.APPLICATION_XML]
    )
    fun getPix(@PathVariable key: String): CreatePixKeyResponse?

    @Delete(
        "/api/v1/pix/keys/{key}", consumes = [MediaType.APPLICATION_XML],
        produces = [MediaType.APPLICATION_XML]
    )
    fun deletePix(@PathVariable key: String, @Body request: DeletePixKeyRequest): DeletePixKeyResponse?

    @Get("/api/v1/pix/keys", produces = [MediaType.APPLICATION_XML])
    fun listAllPix(): PixKeysListResponse?
}