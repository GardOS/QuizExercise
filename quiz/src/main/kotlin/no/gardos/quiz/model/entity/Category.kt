package no.gardos.quiz.model.entity

import javax.persistence.*
import javax.validation.constraints.Size
import org.hibernate.validator.constraints.NotEmpty

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