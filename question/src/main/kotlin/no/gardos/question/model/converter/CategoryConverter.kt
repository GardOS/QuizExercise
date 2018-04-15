package no.gardos.question.model.converter

import no.gardos.question.model.entity.Category
import no.gardos.schema.CategoryDto

class CategoryConverter {
	companion object {
		fun transform(category: Category): CategoryDto {
			return CategoryDto(
					id = category.id,
					name = category.name
			)
		}

		fun transform(categories: Iterable<Category>): List<CategoryDto> {
			return categories.map { transform(it) }
		}
	}
}