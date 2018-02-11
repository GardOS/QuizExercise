package no.gardos.quiz

import javax.persistence.*

@Entity
class CategoryEntity(
        @Column(unique = true)
        var name: String,

        @get:Id @get:GeneratedValue
        var id: Long? = null
)