package br.com.zup.edu.handler

class ClienteNaoEncontradoException(message: String = "Cliente não encontrado"): RuntimeException(message) {
}