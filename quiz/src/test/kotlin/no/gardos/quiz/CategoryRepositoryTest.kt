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
	private lateinit var categoryRepo: CategoryRepository
	var categoryName = "Category"

	private fun createTestCategory(): CategoryEntity {
		return categoryRepo.save(CategoryEntity(categoryName))
	}

	@Before
	fun cleanDatabase() {
		categoryRepo.deleteAll()
	}

	@Test
	fun testInit() {
		assertNotNull(categoryRepo)
	}

	@Test
	fun save_ValidCategory_CategoryCreated() {
		assertEquals(0, categoryRepo.count())
		createTestCategory()
		assertEquals(1, categoryRepo.count())
	}

	@Test
	fun save_ValidCategory_CategoryUpdated() {
		val category = createTestCategory()
		assertEquals(categoryName, category.name)
		categoryRepo.save(category)

		val newCategoryName = "NewCategoryName"
		category.name = newCategoryName
		categoryRepo.save(category)

		val updatedQuiz = categoryRepo.findOne(category.id)
		assertEquals(newCategoryName, updatedQuiz.name)
	}

	@Test
	fun delete_ExistingCategory_CategoryDeleted() {
		val category = createTestCategory()

		assertNotNull(categoryRepo.findOne(category.id))

		categoryRepo.delete(category.id)

		assertNull(categoryRepo.findOne(category.id))
	}

	@Test
	fun findByName_ExistingCategory_CategoryFound() {
		val categoryName = "Sports"
		val category = CategoryEntity(categoryName)
		categoryRepo.save(category)

		assertEquals(category.id, categoryRepo.findByName(categoryName)?.id)
	}

	@Test
	fun findByName_NoCategory_ReturnNull() {
		assertEquals(null, categoryRepo.findByName(categoryName))
	}

	@Test(expected = ConstraintViolationException::class)
	fun sizeConstraint_NameTooLong_ConstraintViolationException() {
		val categoryName = "123456789012345678901234567890123" //33 length
		val category = CategoryEntity(categoryName)
		categoryRepo.save(category)
	}

	@Test(expected = ConstraintViolationException::class)
	fun notEmptyConstraint_NullName_ConstraintViolationException() {
		val categoryName = null
		val category = CategoryEntity(categoryName)
		categoryRepo.save(category)
	}

	@Test(expected = ConstraintViolationException::class)
	fun notEmptyConstraint_EmptyName_ConstraintViolationException() {
		val categoryName = ""
		val category = CategoryEntity(categoryName)
		categoryRepo.save(category)
	}
}