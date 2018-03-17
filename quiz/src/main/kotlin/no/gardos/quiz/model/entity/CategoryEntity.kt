package no.gardos.quiz.model.entity

import javax.persistence.*

@Entity
class CategoryEntity(

		@get:Column(unique = true)
		var name: String? = null,

		@get:Id @get:GeneratedValue
		var id: Long? = null
)