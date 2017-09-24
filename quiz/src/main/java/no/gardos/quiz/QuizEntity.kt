package no.gardos.quiz

import javax.persistence.*
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.Size

@Entity
class QuizEntity(
        var questionText: String,

        @get:Size(min = 1, max = 4)
        var answers: Array<String>,

        @get:Min(1) @get:Max(4)
        var correctAnswer: Int,

        @get:ManyToOne
        @JoinColumn(name = "CategoryEntity_id", nullable = false)
        var category: CategoryEntity,

        @get:Id @get:GeneratedValue
        var id: Long? = null
)