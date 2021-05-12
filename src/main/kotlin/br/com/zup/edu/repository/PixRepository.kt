package br.com.zup.edu.repository

import br.com.zup.edu.model.Pix
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface PixRepository: JpaRepository<Pix, Long> {
    fun existsByKey(chave: String?): Boolean
    fun existsByOwner(chave: String?): Boolean
}