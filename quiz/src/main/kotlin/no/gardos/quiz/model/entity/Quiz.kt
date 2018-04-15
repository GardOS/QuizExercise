package no.gardos.quiz.model.entity

import javax.persistence.*

@Entity
class Quiz(
		@get:Id @get:GeneratedValue
		var id: Long? = null
)