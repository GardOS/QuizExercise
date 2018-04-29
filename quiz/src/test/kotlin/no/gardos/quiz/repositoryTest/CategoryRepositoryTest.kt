package no.gardos.quiz.repositoryTest

import no.gardos.quiz.model.entity.Category
import no.gardos.quiz.model.entity.Question
import org.junit.Assert.*
import org.junit.Test
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.transaction.TransactionSystemException

class CategoryRepositoryTest : RepositoryTestBase() {

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
		assertEquals(defaultCategoryName, category.name)
		categoryRepo.save(category)

		val newCategoryName = "NewCategoryName"
		category.name = newCategoryName
		categoryRepo.save(category)

		val updatedQuestion = categoryRepo.getOne(category.id!!)
		assertEquals(newCategoryName, updatedQuestion.name)
	}

	@Test
	fun delete_ExistingCategory_CategoryDeleted() {
		val category = createTestCategory()

		assertNotNull(categoryRepo.getOne(category.id!!))

		categoryRepo.deleteById(category.id!!)

		assertFalse(categoryRepo.existsById(category.id!!))
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
		assertEquals(null, categoryRepo.findByName(defaultCategoryName))
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

	@Test(expected = TransactionSystemException::class)
	fun sizeConstraint_NameTooLong_ConstraintViolationException() {
		val categoryName = "123456789012345678901234567890123" //33 length
		val category = Category(categoryName)
		categoryRepo.save(category)
	}

	@Test(expected = TransactionSystemException::class)
	fun notEmptyConstraint_NullName_ConstraintViolationException() {
		val categoryName = null
		val category = Category(categoryName)
		categoryRepo.save(category)
	}

	@Test(expected = TransactionSystemException::class)
	fun notEmptyConstraint_EmptyName_ConstraintViolationException() {
		val categoryName = ""
		val category = Category(categoryName)
		categoryRepo.save(category)
	}

	@Test(expected = DataIntegrityViolationException::class)
	fun uniqueConstraint_DuplicateName_ConstraintViolationException() {
		val categoryName = "Category"
		val category = Category(categoryName)
		val duplicateCategory = Category(categoryName)
		categoryRepo.save(category)
		categoryRepo.save(duplicateCategory)
	}
}