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
import javax.validation.ConstraintViolationException

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
	fun save_ValidCategory_CategoryCreated() {
		assertEquals(0, categoryCrud.count())
		createTestCategory()
		assertEquals(1, categoryCrud.count())
	}

	@Test
	fun save_ValidCategory_CategoryUpdated() {
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
	fun delete_ExistingCategory_CategoryDeleted() {
		val category = createTestCategory()

		assertNotNull(categoryCrud.findOne(category.id))

		categoryCrud.delete(category.id)

		assertNull(categoryCrud.findOne(category.id))
	}

	@Test
	fun findByName_ExistingCategory_CategoryFound() {
		val categoryName = "Sports"
		val category = CategoryEntity(categoryName)
		categoryCrud.save(category)

		assertEquals(category.id, categoryCrud.findByName(categoryName)?.id)
	}

	@Test
	fun findByName_NoCategory_ReturnNull() {
		assertEquals(null, categoryCrud.findByName(categoryName))
	}

	@Test(expected = ConstraintViolationException::class)
	fun sizeConstraint_NameTooLong_ConstraintViolationException() {
		val categoryName = "123456789012345678901234567890123" //33 length
		val category = CategoryEntity(categoryName)
		categoryCrud.save(category)
	}
}