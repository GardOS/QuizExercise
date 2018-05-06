package no.gardos.quiz.model.entity

import org.hibernate.validator.constraints.NotEmpty
import javax.persistence.*
import javax.validation.constraints.*

@Entity
class Question(
		@get:NotEmpty
		var questionText: String? = null,

		@get:Size(min = 2, max = 4)
		@get:NotNull
		@get:ElementCollection(targetClass = String::class)
		var answers: List<String>? = null,

		@get:Min(0) @get:Max(3)
		@get:NotNull
		var correctAnswer: Int? = null,

		@get:ManyToOne
		var category: Category? = null,

		@get:Id @get:GeneratedValue
		var id: Long? = null
)