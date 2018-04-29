package no.gardos.quiz.model.entity

import javax.persistence.*
import javax.validation.constraints.NotEmpty

@Entity
class Quiz(
		@get:Column(unique = true)
		@get:NotEmpty
		var name: String? = null,

		@get:ManyToMany
		var questions: List<Question>? = null,

		@get:Id @get:GeneratedValue
		var id: Long? = null
)