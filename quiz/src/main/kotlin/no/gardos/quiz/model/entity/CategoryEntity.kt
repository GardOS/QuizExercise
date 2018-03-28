package no.gardos.quiz.model.entity

import org.hibernate.validator.constraints.NotEmpty
import javax.persistence.*
import javax.validation.constraints.Size

@Entity
class CategoryEntity(

		@get:Column(unique = true)
		@get:Size(max = 32)
		@get:NotEmpty
		var name: String? = null,

		@get: OneToMany(mappedBy = "category")
		var questions: MutableList<QuestionEntity> = ArrayList(),

		@get:Id @get:GeneratedValue
		var id: Long? = null
)