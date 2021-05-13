package br.com.zup.edu.handler

class DeletePixOwnerException(message: String = "Esse não é o dono dessa chave pix"): RuntimeException(message) {
}