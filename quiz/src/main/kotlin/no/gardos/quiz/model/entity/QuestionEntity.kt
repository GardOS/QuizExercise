package no.gardos.quiz.model.entity

import org.hibernate.validator.constraints.NotEmpty
import javax.persistence.*
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.Size

@Entity
class QuestionEntity(
		@get:NotEmpty
		var questionText: String? = null,

		@get:Size(max = 4)
		@get:NotEmpty
		@get:ElementCollection(targetClass = String::class)
		var answers: List<String>? = null,

		@get:Min(0) @get:Max(3)
		var correctAnswer: Int? = null,

		@get:ManyToOne
		var category: CategoryEntity? = null,

		@get:Id @get:GeneratedValue
		var id: Long? = null
)