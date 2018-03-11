package no.gardos.quiz

import no.gardos.quiz.model.entity.QuestionEntity
import no.gardos.quiz.model.repository.QuestionRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class QuestionRepositoryTest {

	@Autowired
	private lateinit var questionCrud: QuestionRepository
	var questionText = "QuestionText"
	var answers = listOf("Wrong", "Wrong", "Correct", "Wrong")
	var correctAnswer = 2 //Don't set to above 3

	private fun createTestQuestion(): QuestionEntity {
		return questionCrud.save(QuestionEntity(questionText, answers, correctAnswer, null))
	}

	@Before
	fun cleanDatabase() {
		questionCrud.deleteAll()
	}

	@Test
	fun testInit() {
		assertNotNull(questionCrud)
	}

	@Test
	fun testCreateQuiz() {
		assertEquals(0, questionCrud.count())
		createTestQuestion()
		assertEquals(1, questionCrud.count())
	}

	@Test
	fun testUpdateQuiz() {
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
	fun testDeleteQuiz() {
		val question = createTestQuestion()

		assertNotNull(questionCrud.findOne(question.id))

		questionCrud.delete(question.id)

		assertNull(questionCrud.findOne(question.id))
	}

	//Todo: test this from APIs with restAssured
//	@Test
//	fun testFindQuestionByCategoryName() {
//		val letterCategory = categoryCrud.save(CategoryEntity("Letters"))
//		val numberCategory = categoryCrud.save(CategoryEntity("Numbers"))
//		questionCrud.save(QuestionEntity("Which is B?", arrayOf("A", "B", "C", "D"), 1, letterCategory))
//		questionCrud.save(QuestionEntity("Which is D?", arrayOf("A", "B", "C", "D"), 3, letterCategory))
//		questionCrud.save(QuestionEntity("Which is 2?", arrayOf("0", "1", "2", "3"), 2, numberCategory))
//
//		val a = questionCrud.findQuizByCategoryName("Letters")
//		assertEquals(2, a.count())
//	}
}