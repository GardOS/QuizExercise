package no.gardos.quiz.repositoryTest

import no.gardos.quiz.model.Quiz
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.springframework.dao.DataIntegrityViolationException
import javax.validation.ConstraintViolationException

class QuizRepositoryTest : RepositoryTestBase() {

	@Before
	@Test
	fun testInit() {
		Assert.assertNotNull(quizRepo)
	}

	@Test
	fun save_ValidQuiz_QuizCreated() {
		assertEquals(0, quizRepo.count())
		createTestQuiz()
		assertEquals(1, quizRepo.count())
	}

	@Test
	fun save_ExistingQuiz_QuizUpdated() {
		val newName = "newName"
		val quiz = createTestQuiz()

		assertTrue(quiz.name != newName)

		quiz.name = newName
		quizRepo.save(quiz)

		val updatedQuiz = quizRepo.getOne(quiz.id!!.toLong())
		assertEquals(newName, updatedQuiz.name)
	}

	@Test
	fun delete_ExistingQuiz_QuizDeleted() {
		val quiz = createTestQuiz(questions = null)

		Assert.assertNotNull(quizRepo.findOne(quiz.id!!.toLong()))

		quizRepo.delete(quiz.id!!.toLong())

		Assert.assertFalse(quizRepo.exists(quiz.id!!.toLong()))
	}

	@Test(expected = ConstraintViolationException::class)
	fun notEmptyConstraint_NullName_ConstraintViolationException() {
		val quizName = null
		val quiz = Quiz(quizName)
		quizRepo.save(quiz)
	}

	@Test(expected = ConstraintViolationException::class)
	fun notEmptyConstraint_EmptyName_ConstraintViolationException() {
		val quizName = ""
		val quiz = Quiz(quizName)
		quizRepo.save(quiz)
	}

	@Test(expected = DataIntegrityViolationException::class)
	fun uniqueConstraint_DuplicateName_ConstraintViolationException() {
		val quizName = "Quiz"
		val quiz = Quiz(quizName)
		val duplicateQuiz = Quiz(quizName)
		quizRepo.save(quiz)
		quizRepo.save(duplicateQuiz)
	}

	@Ignore //Hibernate don't care about id received, auto-generates anyway
	@Test(expected = ConstraintViolationException::class)
	fun idConstraint_IdIsSpecified_ConstraintViolationException() {
		createTestQuiz(id = 1234)
	}

	@Test()
	fun relationConstraints_ExistingRelations_CategoryReturned() {
		val quiz = createTestQuiz()
		assertEquals(defaultCategoryName, quiz.questions?.first()?.category?.name)
	}
}