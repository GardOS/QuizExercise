package no.gardos.quiz

import javax.persistence.*

@Entity
class CategoryEntity(
        var name: String,

        @get:OneToMany(mappedBy = "id")
        var quizEntities: MutableList<QuizEntity> = ArrayList(),

        @get:Id @get:GeneratedValue
        var id: Long? = null
)