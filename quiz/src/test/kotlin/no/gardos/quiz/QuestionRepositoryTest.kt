package no.gardos.quiz

import no.gardos.quiz.model.entity.CategoryEntity
import no.gardos.quiz.model.entity.QuestionEntity
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
@SpringBootTest
class QuestionRepositoryTest {

	@Autowired
	private lateinit var questionCrud: QuestionRepository
	@Autowired
	private lateinit var categoryCrud: CategoryRepository
	var questionText = "QuestionText"
	var answers = listOf("Wrong", "Wrong", "Correct", "Wrong")
	var correctAnswer = 2 //Don't set to above 3

	private fun createTestQuestion(
			questionText: String? = this.questionText,
			answers: List<String>? = this.answers,
			correctAnswer: Int? = this.correctAnswer,
			id: Long? = null)
			: QuestionEntity {
		return questionCrud.save(QuestionEntity(questionText, answers, correctAnswer, null, id))
	}

	@Before
	fun cleanDatabase() {
		questionCrud.deleteAll()
	}

	@Before
	fun testInit() {
		assertNotNull(questionCrud)
	}

	@Test
	fun save_ValidQuestion_QuestionCreated() {
		assertEquals(0, questionCrud.count())
		createTestQuestion()
		assertEquals(1, questionCrud.count())
	}

	@Test
	fun save_ExistingQuestion_QuestionUpdated() {
		val question = createTestQuestion()
		assertEquals(questionText, question.questionText)
		questionCrud.save(question)

		val newQuestionText = "NewQuestionText"
		question.questionText = newQuestionText
		questionCrud.save(question)

		val updatedQuiz = questionCrud.findOne(question.id)
		assertEquals(newQuestionText, updatedQuiz.questionText)
	}

	@Test
	fun delete_ExistingQuestion_QuestionDeleted() {
		val question = createTestQuestion()

		assertNotNull(questionCrud.findOne(question.id))

		questionCrud.delete(question.id)

		assertNull(questionCrud.findOne(question.id))
	}

	@Test(expected = ConstraintViolationException::class)
	fun notEmptyConstraint_NullQuestionText_ConstraintViolationException() {
		val question = createTestQuestion(questionText = null)
		questionCrud.save(question)
	}

	@Test(expected = ConstraintViolationException::class)
	fun notEmptyConstraint_BlankQuestionText_ConstraintViolationException() {
		val question = createTestQuestion(questionText = "")
		questionCrud.save(question)
	}

	@Test(expected = ConstraintViolationException::class)
	fun notEmptyConstraint_NullAnswers_ConstraintViolationException() {
		val question = createTestQuestion(answers = null)
		questionCrud.save(question)
	}

	@Test(expected = ConstraintViolationException::class)
	fun notEmptyConstraint_NoAnswers_ConstraintViolationException() {
		val question = createTestQuestion(answers = listOf())
		questionCrud.save(question)
	}

	@Test(expected = ConstraintViolationException::class)
	fun sizeConstraint_TooManyAnswers_ConstraintViolationException() {
		val question = createTestQuestion(answers = listOf("1", "2", "3", "4", "5"))
		questionCrud.save(question)
	}

	@Test(expected = ConstraintViolationException::class)
	fun minConstraint_CorrectAnswerTooSmall_ConstraintViolationException() {
		val question = createTestQuestion(correctAnswer = -1)
		questionCrud.save(question)
	}

	@Test(expected = ConstraintViolationException::class)
	fun minConstraint_CorrectAnswerTooBig_ConstraintViolationException() {
		val question = createTestQuestion(correctAnswer = 4)
		questionCrud.save(question)
	}

	@Test(expected = ConstraintViolationException::class)
	fun idConstraint_IdIsSpecified_ConstraintViolationException() {
		val question = createTestQuestion(id = 4)
		questionCrud.save(question)
	}

	@Test
	fun findByCategoryName_QuestionsWithCategoriesExist_QuizFound() {
		val letterCategory = categoryCrud.save(CategoryEntity("Letters"))
		val numberCategory = categoryCrud.save(CategoryEntity("Numbers"))
		questionCrud.save(QuestionEntity("Which is B?", listOf("A", "B", "C", "D"), 1, letterCategory))
		questionCrud.save(QuestionEntity("Which is D?", listOf("A", "B", "C", "D"), 3, letterCategory))
		questionCrud.save(QuestionEntity("Which is 2?", listOf("0", "1", "2", "3"), 2, numberCategory))

		val a = questionCrud.findQuizByCategoryName("Letters")
		assertEquals(2, a.count())
	}
}