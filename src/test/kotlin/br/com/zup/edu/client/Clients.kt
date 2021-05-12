package br.com.zup.edu.client

import br.com.zup.edu.KeymanagergrpcServiceGrpc
import io.grpc.ManagedChannel

import io.micronaut.grpc.server.GrpcServerChannel

import io.micronaut.grpc.annotation.GrpcChannel

import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory


@Factory
internal class Clients {
    @Bean
    fun blockingStub(
        @GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel?
    ): KeymanagergrpcServiceGrpc.KeymanagergrpcServiceBlockingStub {
        return KeymanagergrpcServiceGrpc.newBlockingStub(
            channel
        )
    }
}