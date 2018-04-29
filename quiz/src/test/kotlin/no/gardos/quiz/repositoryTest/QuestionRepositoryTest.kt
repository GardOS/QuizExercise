package no.gardos.quiz.repositoryTest

import no.gardos.quiz.model.entity.Category
import no.gardos.quiz.model.entity.Question
import org.junit.Assert.*
import org.junit.Ignore
import org.junit.Test
import org.springframework.transaction.TransactionSystemException

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

		questionRepo.deleteById(question.id!!)

		assertFalse(questionRepo.existsById(question.id!!))
	}

	@Test(expected = TransactionSystemException::class)
	fun notEmptyConstraint_NullQuestionText_TransactionSystemException() {
		val question = createTestQuestion(questionText = null)
		questionRepo.save(question)
	}

	@Test(expected = TransactionSystemException::class)
	fun notEmptyConstraint_BlankQuestionText_TransactionSystemException() {
		val question = createTestQuestion(questionText = "")
		questionRepo.save(question)
	}

	@Test(expected = TransactionSystemException::class)
	fun notEmptyConstraint_NullAnswers_TransactionSystemException() {
		val question = createTestQuestion(answers = null)
		questionRepo.save(question)
	}

	@Test(expected = TransactionSystemException::class)
	fun notEmptyConstraint_NoAnswers_TransactionSystemException() {
		val question = createTestQuestion(answers = listOf())
		questionRepo.save(question)
	}

	@Test(expected = TransactionSystemException::class)
	fun sizeConstraint_TooFewAnswers_TransactionSystemException() {
		val question = createTestQuestion(answers = listOf("1"))
		questionRepo.save(question)
	}

	@Test(expected = TransactionSystemException::class)
	fun sizeConstraint_TooManyAnswers_TransactionSystemException() {
		val question = createTestQuestion(answers = listOf("1", "2", "3", "4", "5"))
		questionRepo.save(question)
	}

	@Test(expected = TransactionSystemException::class)
	fun minConstraint_NullCorrectAnswer_TransactionSystemException() {
		val question = createTestQuestion(correctAnswer = null)
		questionRepo.save(question)
	}

	@Test(expected = TransactionSystemException::class)
	fun minConstraint_CorrectAnswerTooSmall_TransactionSystemException() {
		val question = createTestQuestion(correctAnswer = -1)
		questionRepo.save(question)
	}

	@Test(expected = TransactionSystemException::class)
	fun minConstraint_CorrectAnswerTooBig_TransactionSystemException() {
		val question = createTestQuestion(correctAnswer = 4)
		questionRepo.save(question)
	}

	@Ignore //Todo: Suddenly okay to insert Id. Is this a problem?
	@Test(expected = TransactionSystemException::class)
	fun idConstraint_IdIsSpecified_TransactionSystemException() {
		val question = createTestQuestion(id = 1234)
		questionRepo.save(question)
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