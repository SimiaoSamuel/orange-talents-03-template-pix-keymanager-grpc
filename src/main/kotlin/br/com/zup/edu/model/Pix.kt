package br.com.zup.edu.model

import br.com.zup.edu.KeyType
import br.com.zup.edu.OpenClass
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
    val owner: String
)