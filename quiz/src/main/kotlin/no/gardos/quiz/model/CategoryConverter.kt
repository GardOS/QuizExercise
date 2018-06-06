package no.gardos.quiz.model

import no.gardos.schema.CategoryDto

class CategoryConverter {
	companion object {
		fun transform(category: Category): CategoryDto {
			return CategoryDto(
					id = category.id.toString(),
					name = category.name
			)
		}

		fun transform(categories: Iterable<Category>): List<CategoryDto> {
			return categories.map { transform(it) }
		}
	}
}