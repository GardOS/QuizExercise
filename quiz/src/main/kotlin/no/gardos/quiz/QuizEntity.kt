package no.gardos.quiz

import javax.persistence.*
import javax.validation.constraints.*

@Entity
class QuizEntity(
        var questionText: String,

        @get:Size(min = 1, max = 4) @get:NotNull
        var answers: Array<String>,

        @get:Min(0) @get:Max(3)
        var correctAnswer: Int,

        @get:OneToOne @get:NotNull
        var category: CategoryEntity,

        @get:Id @get:GeneratedValue
        var id: Long? = null
)