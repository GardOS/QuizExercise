package no.gardos.quiz.model.entity

import javax.persistence.*
import javax.validation.constraints.*

@Entity
class QuestionEntity(
		@get:NotNull
		var questionText: String? = null,

		@get:Size(min = 1, max = 4)
		@get:NotNull
		@get:ElementCollection(targetClass = String::class)
		var answers: List<String>? = null,

		@get:Min(0) @get:Max(3)
		var correctAnswer: Int? = null,

		@get:OneToOne
		var category: CategoryEntity? = null,

		@get:Id @get:GeneratedValue
		var id: Long? = null
)