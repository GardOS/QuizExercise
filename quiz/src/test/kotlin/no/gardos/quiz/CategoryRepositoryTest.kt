package no.gardos.quiz

import no.gardos.quiz.model.entity.CategoryEntity
import no.gardos.quiz.model.repository.CategoryRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class CategoryRepositoryTest {

	@Autowired
	private lateinit var categoryCrud: CategoryRepository
	var categoryName = "Category"

	private fun createTestCategory(): CategoryEntity {
		return categoryCrud.save(CategoryEntity(categoryName))
	}

	@Before
	fun cleanDatabase() {
		categoryCrud.deleteAll()
	}

	@Test
	fun testInit() {
		assertNotNull(categoryCrud)
	}

	@Test
	fun testCreateCategory() {
		assertEquals(0, categoryCrud.count())
		createTestCategory()
		assertEquals(1, categoryCrud.count())
	}

	@Test
	fun testUpdateCategory() {
		val category = createTestCategory()
		assertEquals(categoryName, category.name)
		categoryCrud.save(category)

		val newCategoryName = "NewCategoryName"
		category.name = newCategoryName
		categoryCrud.save(category)

		val updatedQuiz = categoryCrud.findOne(category.id)
		assertEquals(newCategoryName, updatedQuiz.name)
	}

	@Test
	fun testDeleteCategory() {
		val category = createTestCategory()

		assertNotNull(categoryCrud.findOne(category.id))

		categoryCrud.delete(category.id)

		assertNull(categoryCrud.findOne(category.id))
	}

	@Test
	fun testFindCategoryByName() {
		val categoryName = "Sports"
		val category = CategoryEntity(categoryName)
		categoryCrud.save(category)

		assertEquals(category.id, categoryCrud.findByName(categoryName).id)
	}
}