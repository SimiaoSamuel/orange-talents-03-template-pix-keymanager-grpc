package br.com.zup.edu.handler

class PixOwnerException(message: String = "Esse não é o dono dessa chave pix"): RuntimeException(message) {
}