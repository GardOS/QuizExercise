package no.gardos.question

import no.gardos.question.model.entity.Category
import no.gardos.question.model.entity.Question
import no.gardos.question.model.repository.CategoryRepository
import no.gardos.question.model.repository.QuestionRepository
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
	@Autowired
	private lateinit var questionRepo: QuestionRepository
	var categoryName = "Category"

	private fun createTestCategory(name: String = categoryName): Category {
		return categoryRepo.save(Category(name))
	}

	@Before
	fun cleanDatabase() {
		questionRepo.deleteAll()
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

		val updatedQuestion = categoryRepo.findOne(category.id)
		assertEquals(newCategoryName, updatedQuestion.name)
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
		val category = Category(categoryName)
		categoryRepo.save(category)

		assertEquals(category.id, categoryRepo.findByName(categoryName)?.id)
	}

	@Test
	fun findByName_NoCategory_ReturnNull() {
		assertEquals(null, categoryRepo.findByName(categoryName))
	}

	@Test
	fun findByQuestionsIsNotNull_ExistingRelations_CorrectCategoriesReturned() {
		val firstCategory = createTestCategory("firstCategory")
		val secondCategory = createTestCategory("secondCategory")

		val question = questionRepo.save(
				Question(
						questionText = "What is 1+1?",
						answers = listOf("0", "1", "2", "3"),
						correctAnswer = 2,
						category = firstCategory
				)
		)

		assertEquals(firstCategory.id, categoryRepo.findByQuestionsIsNotNull().first().id)

		question.category = secondCategory
		questionRepo.save(question)

		assertEquals(secondCategory.id, categoryRepo.findByQuestionsIsNotNull().first().id)
	}

	@Test(expected = ConstraintViolationException::class)
	fun sizeConstraint_NameTooLong_ConstraintViolationException() {
		val categoryName = "123456789012345678901234567890123" //33 length
		val category = Category(categoryName)
		categoryRepo.save(category)
	}

	@Test(expected = ConstraintViolationException::class)
	fun notEmptyConstraint_NullName_ConstraintViolationException() {
		val categoryName = null
		val category = Category(categoryName)
		categoryRepo.save(category)
	}

	@Test(expected = ConstraintViolationException::class)
	fun notEmptyConstraint_EmptyName_ConstraintViolationException() {
		val categoryName = ""
		val category = Category(categoryName)
		categoryRepo.save(category)
	}
}