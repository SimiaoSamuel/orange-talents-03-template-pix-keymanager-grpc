package br.com.zup.edu.model

import br.com.zup.edu.AccountType
import br.com.zup.edu.KeyType
import br.com.zup.edu.OpenClass
import br.com.zup.edu.dto.DeletePixKeyRequest
import br.com.zup.edu.dto.KeyTypeToValidate
import br.com.zup.edu.dto.NovaChavePix
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
    val accountType: AccountType,
    val owner: String,
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Pix

        if (key != other.key) return false
        if (owner != other.owner) return false

        return true
    }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + owner.hashCode()
        return result
    }

    fun toNovaChavePix(): NovaChavePix {
        return NovaChavePix(owner,KeyTypeToValidate.valueOf(keyType.name),key,accountType)
    }
}
