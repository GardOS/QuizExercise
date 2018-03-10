package no.gardos.quiz

class CategoryConverter {

	companion object {
		private fun transform(category: CategoryEntity): CategoryDto {
			return CategoryDto(
					id = category.id?.toString(),
					name = category.name
			)
		}

		fun transform(categories: Iterable<CategoryEntity>): List<CategoryDto> {
			return categories.map { transform(it) }
		}
	}
}