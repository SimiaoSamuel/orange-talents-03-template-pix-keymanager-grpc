package br.com.zup.edu.enum

import br.com.zup.edu.dto.KeyTypeToValidate
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class KeyTypeTest() {

    @Test
    fun `deve retornar false se chave invalida referente ao cpf `() {
        val cpf = KeyTypeToValidate.CPF
        with(cpf) {
            Assertions.assertFalse(this.validate(""))
            Assertions.assertFalse(this.validate("487"))
            Assertions.assertFalse(this.validate("3741116505"))
            Assertions.assertFalse(this.validate(null))
        }
    }

    @Test
    fun `deve retornar true se chave for valida referente ao cpf `() {
        val cpf = KeyTypeToValidate.CPF
        with(cpf) {
            Assertions.assertTrue(this.validate("37411165050"))
            Assertions.assertTrue(this.validate("11070524085"))
        }
    }

    @Test
    fun `deve retornar false se chave for invalida referente ao email `() {
        val email = KeyTypeToValidate.EMAIL
        with(email) {
            Assertions.assertFalse(this.validate("samuel"))
            Assertions.assertFalse(this.validate(null))
            Assertions.assertFalse(this.validate("xpto@gmail"))
            Assertions.assertFalse(this.validate("xpto@gmail.combr"))
            Assertions.assertFalse(this.validate("xpto@gmailcombr"))
        }
    }

    @Test
    fun `deve retornar true se chave for valida referente ao email `() {
        val email = KeyTypeToValidate.EMAIL
        with(email) {
            Assertions.assertTrue(this.validate("xpto@gmail.com.br"))
            Assertions.assertTrue(this.validate("xpto@gmail.com"))
            Assertions.assertTrue(this.validate("xpto@gmail.com.eu"))
            Assertions.assertTrue(this.validate("xpto@gmail.com.us"))
        }
    }

    @Test
    fun `deve retornar false se chave for invalida referente ao phone `() {
        val phone = KeyTypeToValidate.PHONE
        with(phone) {
            Assertions.assertFalse(this.validate("4"))
            Assertions.assertFalse(this.validate(null))
            Assertions.assertFalse(this.validate("40028922"))
            Assertions.assertFalse(this.validate("5511940028922"))
        }
    }

    @Test
    fun `deve retornar true se chave for valida referente ao phone `() {
        val phone = KeyTypeToValidate.PHONE
        with(phone) {
            Assertions.assertTrue(this.validate("+5511940028922"))
            Assertions.assertTrue(this.validate("+5511982218922"))
        }
    }

    @Test
    fun `deve retornar false se chave for invalida referente ao random `() {
        val random = KeyTypeToValidate.RANDOM
        with(random) {
            Assertions.assertFalse(this.validate("bfibdsf"))
            Assertions.assertFalse(this.validate("dhhsoisd"))
            Assertions.assertFalse(this.validate("askjpdjsadpasdpa"))
        }
    }

    @Test
    fun `deve retornar true se chave for valida referente ao random `() {
        val random = KeyTypeToValidate.RANDOM
        with(random) {
            Assertions.assertTrue(this.validate(null))
            Assertions.assertTrue(this.validate(""))
            Assertions.assertTrue(this.validate(" "))
        }
    }
}