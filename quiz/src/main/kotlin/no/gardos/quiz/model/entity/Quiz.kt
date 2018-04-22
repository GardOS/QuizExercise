package no.gardos.quiz.model.entity

import org.hibernate.validator.constraints.NotEmpty
import javax.persistence.*

@Entity
class Quiz(
		@get:Column(unique = true)
		@get:NotEmpty
		var name: String? = null,

		@get: OneToMany
		var questions: MutableList<Question>? = null,

		@get:Id @get:GeneratedValue
		var id: Long? = null
)