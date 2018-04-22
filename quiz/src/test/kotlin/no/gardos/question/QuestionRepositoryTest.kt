package no.gardos.question

import no.gardos.quiz.QuizApplication
import no.gardos.quiz.model.entity.Category
import no.gardos.quiz.model.entity.Question
import no.gardos.quiz.model.repository.CategoryRepository
import no.gardos.quiz.model.repository.QuestionRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import javax.validation.ConstraintViolationException

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [(QuizApplication::class)])
class QuestionRepositoryTest {

	@Autowired
	private lateinit var questionRepo: QuestionRepository
	@Autowired
	private lateinit var categoryRepo: CategoryRepository
	var questionText = "QuestionText"
	var answers = listOf("Wrong", "Wrong", "Correct", "Wrong")
	var correctAnswer = 2 //Don't set to above 3

	private fun createTestQuestion(
			questionText: String? = this.questionText,
			answers: List<String>? = this.answers,
			correctAnswer: Int? = this.correctAnswer,
			id: Long? = null)
			: Question {
		return questionRepo.save(Question(questionText, answers, correctAnswer, null, id))
	}

	@Before
	fun cleanDatabase() {
		questionRepo.deleteAll()
		categoryRepo.deleteAll()
	}

	@Before
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
		assertEquals(questionText, question.questionText)
		questionRepo.save(question)

		val newQuestionText = "NewQuestionText"
		question.questionText = newQuestionText
		questionRepo.save(question)

		val updatedQuestion = questionRepo.findOne(question.id)
		assertEquals(newQuestionText, updatedQuestion.questionText)
	}

	@Test
	fun delete_ExistingQuestion_QuestionDeleted() {
		val question = createTestQuestion()

		assertNotNull(questionRepo.findOne(question.id))

		questionRepo.delete(question.id)

		assertNull(questionRepo.findOne(question.id))
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
		val question = createTestQuestion(id = 4)
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