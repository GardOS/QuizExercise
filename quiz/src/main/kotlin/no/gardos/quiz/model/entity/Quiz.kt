package no.gardos.quiz.model.entity

import org.jetbrains.annotations.NotNull
import javax.persistence.*

@Entity
class Quiz(
		@get:Id @get:GeneratedValue
		var id: Long? = null,

		@NotNull
		var name: String? = null,

		@NotNull
		@get: OneToMany
		var questions: MutableList<Question>? = null
)