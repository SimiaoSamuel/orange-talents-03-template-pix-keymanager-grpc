package br.com.zup.edu.handler

class PixNaoEncontradoException(message: String = "Pix não encontrado no sistema") : RuntimeException(message) {
}