package no.gardos.question.model.converter

import no.gardos.question.model.dto.CategoryDto
import no.gardos.question.model.entity.CategoryEntity

class CategoryConverter {
	companion object {
		fun transform(category: CategoryEntity): CategoryDto {
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