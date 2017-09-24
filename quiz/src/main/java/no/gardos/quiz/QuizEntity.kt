package no.gardos.quiz

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class QuizEntity(
        var questionText: String,

        @get:Id @get:GeneratedValue
        var id: Long? = null
)