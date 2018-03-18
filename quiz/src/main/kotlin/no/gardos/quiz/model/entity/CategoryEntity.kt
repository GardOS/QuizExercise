package no.gardos.quiz.model.entity

import javax.persistence.*
import javax.validation.constraints.Size

@Entity
class CategoryEntity(

		@get:Column(unique = true) @get:Size(max = 32)
		var name: String? = null,

		@get:Id @get:GeneratedValue
		var id: Long? = null
)