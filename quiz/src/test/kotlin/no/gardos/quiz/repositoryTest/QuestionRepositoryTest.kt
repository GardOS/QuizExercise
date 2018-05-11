package no.gardos.quiz.repositoryTest

import no.gardos.quiz.model.entity.Category
import no.gardos.quiz.model.entity.Question
import org.junit.Assert.*
import org.junit.Test
import javax.validation.ConstraintViolationException

class QuestionRepositoryTest : RepositoryTestBase() {

	@Test
	fun testInit() {
		assertNotNull(questionRepo)
	}

	@Test
	fun save_ValidQuestion_QuestionCreated() {
		assertEquals(0, questionRepo.count())
		createTestQuestion()
		assertEquals(1, questionRepo.count())
	}

	@Test
	fun save_ExistingQuestion_QuestionUpdated() {
		val question = createTestQuestion()
		assertEquals(defaultQuestionText, question.questionText)

		val newQuestionText = "NewQuestionText"
		question.questionText = newQuestionText
		questionRepo.save(question)

		val updatedQuestion = questionRepo.getOne(question.id!!)
		assertEquals(newQuestionText, updatedQuestion.questionText)
	}

	@Test
	fun delete_ExistingQuestion_QuestionDeleted() {
		val question = createTestQuestion()

		assertNotNull(questionRepo.getOne(question.id!!))

		questionRepo.delete(question.id!!)

		assertFalse(questionRepo.exists(question.id!!))
	}

	@Test(expected = ConstraintViolationException::class)
	fun notEmptyConstraint_NullQuestionText_ConstraintViolationException() {
		val question = createTestQuestion(questionText = null)
		questionRepo.save(question)
	}

	@Test(expected = ConstraintViolationException::class)
	fun notEmptyConstraint_BlankQuestionText_ConstraintViolationException() {
		val question = createTestQuestion(questionText = "")
		questionRepo.save(question)
	}

	@Test(expected = ConstraintViolationException::class)
	fun notEmptyConstraint_NullAnswers_ConstraintViolationException() {
		val question = createTestQuestion(answers = null)
		questionRepo.save(question)
	}

	@Test(expected = ConstraintViolationException::class)
	fun notEmptyConstraint_NoAnswers_ConstraintViolationException() {
		val question = createTestQuestion(answers = listOf())
		questionRepo.save(question)
	}

	@Test(expected = ConstraintViolationException::class)
	fun sizeConstraint_TooFewAnswers_ConstraintViolationException() {
		val question = createTestQuestion(answers = listOf("1"))
		questionRepo.save(question)
	}

	@Test(expected = ConstraintViolationException::class)
	fun sizeConstraint_TooManyAnswers_ConstraintViolationException() {
		val question = createTestQuestion(answers = listOf("1", "2", "3", "4", "5"))
		questionRepo.save(question)
	}

	@Test(expected = ConstraintViolationException::class)
	fun minConstraint_NullCorrectAnswer_ConstraintViolationException() {
		val question = createTestQuestion(correctAnswer = null)
		questionRepo.save(question)
	}

	@Test(expected = ConstraintViolationException::class)
	fun minConstraint_CorrectAnswerTooSmall_ConstraintViolationException() {
		val question = createTestQuestion(correctAnswer = -1)
		questionRepo.save(question)
	}

	@Test(expected = ConstraintViolationException::class)
	fun minConstraint_CorrectAnswerTooBig_ConstraintViolationException() {
		val question = createTestQuestion(correctAnswer = 4)
		questionRepo.save(question)
	}

	@Test(expected = ConstraintViolationException::class)
	fun idConstraint_IdIsSpecified_ConstraintViolationException() {
		createTestQuestion(id = 1234)
	}

	@Test
	fun findQuestionByCategoryName_QuestionsWithCategoriesExist_QuestionFound() {
		val letterCategory = categoryRepo.save(Category("Letters"))
		val numberCategory = categoryRepo.save(Category("Numbers"))
		questionRepo.save(Question("Which is B?", listOf("A", "B", "C", "D"), 1, letterCategory))
		questionRepo.save(Question("Which is D?", listOf("A", "B", "C", "D"), 3, letterCategory))
		questionRepo.save(Question("Which is 2?", listOf("0", "1", "2", "3"), 2, numberCategory))

		val questions = questionRepo.findQuestionByCategoryName(letterCategory.name)
		assertEquals(2, questions.count())
	}

	@Test
	fun findQuestionByCategoryId_QuestionsWithCategoriesExist_QuestionFound() {
		val letterCategory = categoryRepo.save(Category("Letters"))
		val numberCategory = categoryRepo.save(Category("Numbers"))
		questionRepo.save(Question("Which is B?", listOf("A", "B", "C", "D"), 1, letterCategory))
		questionRepo.save(Question("Which is D?", listOf("A", "B", "C", "D"), 3, letterCategory))
		questionRepo.save(Question("Which is 2?", listOf("0", "1", "2", "3"), 2, numberCategory))

		val questions = questionRepo.findQuestionByCategoryId(letterCategory.id)
		assertEquals(2, questions.count())
	}
}