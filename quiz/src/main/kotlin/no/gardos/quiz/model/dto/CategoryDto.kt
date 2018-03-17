package no.gardos.quiz.model.dto

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import no.gardos.quiz.model.entity.CategoryEntity

@ApiModel("DTO for Category. It represent a Category entity")
data class CategoryDto(
		@ApiModelProperty("The name of the Category")
		var name: String? = null,

		@ApiModelProperty("Category id")
		var id: Long? = null
)

class CategoryConverter {
	companion object {
		private fun transform(category: CategoryEntity): CategoryDto {
			return CategoryDto(
					id = category.id,
					name = category.name
			)
		}

		fun transform(categories: Iterable<CategoryEntity>): List<CategoryDto> {
			return categories.map { transform(it) }
		}
	}
}