package no.gardos.quiz

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@RunWith(SpringRunner::class)
@DataJpaTest
@Transactional(propagation = Propagation.NEVER) //See if this throws errors
class QuizRepositoryTest {

	@Autowired
	private lateinit var quizCrud: QuizRepository
	@Autowired
	private lateinit var categoryCrud: CategoryRepository
	var categoryName = "Category"
	var questionText = "QuestionText"
	var answers = arrayOf("Wrong", "Wrong", "Correct", "Wrong")
	var correctAnswer = 2 //Don't set to above 3

	private fun createTestQuiz(categoryEntity: CategoryEntity): QuizEntity {
		return quizCrud.save(QuizEntity(questionText, answers, correctAnswer, categoryEntity))
	}

	private fun createTestCategory(): CategoryEntity {
		return categoryCrud.save(CategoryEntity(categoryName))
	}

	private fun createTestQuizAndTestCategory(): QuizEntity {
		var category = createTestCategory()
		var quiz = createTestQuiz(category)
		categoryCrud.save(category)
		return quiz
	}

	@Before
	fun cleanDatabase(){
		quizCrud.deleteAll()
		categoryCrud.deleteAll()
	}

	@Test
	fun testInit() {
		assertNotNull(quizCrud)
		assertNotNull(categoryCrud)
	}

	@Test
	fun testCreateQuiz() {
		assertEquals(0, quizCrud.count())
		createTestQuizAndTestCategory()
		assertEquals(1, quizCrud.count())
	}

	@Test
	fun testCreateCategory(){
		assertEquals(0, categoryCrud.count())
		createTestCategory()
		assertEquals(1, categoryCrud.count())
	}

	@Test
	fun testUpdateQuiz() {
		var quiz = createTestQuizAndTestCategory()
		assertEquals(questionText, quiz.questionText)
		quizCrud.save(quiz)

		val newQuestionText = "NewQuestionText"
		quiz.questionText = newQuestionText
		quizCrud.save(quiz)

		var updatedQuiz = quizCrud.findOne(quiz.id)
		assertEquals(newQuestionText, updatedQuiz.questionText)
	}

	@Test
	fun testUpdateCategory() {
		var category = createTestCategory()
		assertEquals(categoryName, category.name)
		categoryCrud.save(category)

		val newCategoryName = "NewCategoryName"
		category.name = newCategoryName
		categoryCrud.save(category)

		var updatedQuiz = categoryCrud.findOne(category.id)
		assertEquals(newCategoryName, updatedQuiz.name)
	}

	@Test
	fun testDeleteQuiz() {
		var quiz = createTestQuizAndTestCategory()

		assertNotNull(quizCrud.findOne(quiz.id))

		quizCrud.delete(quiz.id)

		assertNull(quizCrud.findOne(quiz.id))
	}

	@Test
	fun testDeleteCategory() {
		var category = createTestCategory()

		assertNotNull(categoryCrud.findOne(category.id))

		categoryCrud.delete(category.id)

		assertNull(categoryCrud.findOne(category.id))
	}

	@Test
	fun testFindCategoryByName(){
		val categoryName = "Sports"
		var category = CategoryEntity(categoryName)
		categoryCrud.save(category)

		assertEquals(category.id, categoryCrud.findByName(categoryName).id)
	}

	@Test
	fun testGetQuizWithCategory() {
		var letterCategory = categoryCrud.save(CategoryEntity("Letters"))
		var numberCategory = categoryCrud.save(CategoryEntity("Numbers"))
		quizCrud.save(QuizEntity("Which is B?", arrayOf("A", "B", "C", "D"), 1, letterCategory))
		quizCrud.save(QuizEntity("Which is D?", arrayOf("A", "B", "C", "D"), 3, letterCategory))
		quizCrud.save(QuizEntity("Which is 2?", arrayOf("0", "1", "2", "3"), 2, numberCategory))

		var a = quizCrud.findByCategoryName("Letters")
		assertEquals(2, a.count())
	}
}

@SpringBootApplication
class TestApplication