package br.com.zup.edu.handler

class PixDuplicadoException(message: String = "Valor do Pix já existe"): RuntimeException(message) {
}