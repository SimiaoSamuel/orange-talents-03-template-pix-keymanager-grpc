package br.com.zup.edu.model

import br.com.zup.edu.AccountType
import br.com.zup.edu.KeyType
import br.com.zup.edu.OpenClass
import br.com.zup.edu.dto.*
import io.micronaut.data.annotation.Embeddable
import java.time.LocalDateTime
import javax.persistence.*

@OpenClass
@Entity
class Pix(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,
    @Enumerated(EnumType.STRING)
    val keyType: KeyType,
    val key: String,
    @Enumerated(EnumType.STRING)
    val tipoConta: AccountType,
    @Embedded val owner: Owner,
    @Embedded val conta: BankAccount,
    val createdAt: LocalDateTime
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Pix

        if (key != other.key) return false

        return true
    }

    override fun hashCode(): Int {
        return key.hashCode()
    }
}
