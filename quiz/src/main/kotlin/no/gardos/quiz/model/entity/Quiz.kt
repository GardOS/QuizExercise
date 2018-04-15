package no.gardos.quiz.model.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class Quiz(
		@get:Id @get:GeneratedValue
		var id: Long? = null
)