package no.gardos.question.model.entity

import org.hibernate.validator.constraints.NotEmpty
import javax.persistence.*
import javax.validation.constraints.Size

@Entity
class Category(

		@get:Column(unique = true)
		@get:Size(max = 32)
		@get:NotEmpty
		var name: String? = null,

		@get: OneToMany(mappedBy = "category")
		var questions: MutableList<Question> = ArrayList(),

		@get:Id @get:GeneratedValue
		var id: Long? = null
)