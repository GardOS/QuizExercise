package no.gardos.quiz.repositoryTest

import no.gardos.quiz.QuizApplication
import no.gardos.quiz.model.*
import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [(QuizApplication::class)])
abstract class RepositoryTestBase {

	@Autowired
	protected lateinit var categoryRepo: CategoryRepository
	@Autowired
	protected lateinit var questionRepo: QuestionRepository
	@Autowired
	protected lateinit var quizRepo: QuizRepository

	@Before
	fun cleanDatabase() {
		quizRepo.deleteAll()
		questionRepo.deleteAll()
		categoryRepo.deleteAll()
	}

	//Category
	var defaultCategoryName = "Category"

	protected fun createTestCategory(name: String = defaultCategoryName): Category {
		return categoryRepo.save(Category(name))
	}

	//Question
	var defaultQuestionText = "QuestionText"
	var defaultAnswers = listOf("Wrong", "Wrong", "Correct", "Wrong")
	var defaultCorrectAnswer = 2 //Don't set to above 3

	protected fun createTestQuestion(
			questionText: String? = defaultQuestionText,
			answers: List<String>? = defaultAnswers,
			correctAnswer: Int? = defaultCorrectAnswer,
			category: Category? = createTestCategory(),
			id: Long? = null
	): Question {
		return questionRepo.save(Question(questionText, answers, correctAnswer, category, id))
	}

	//Quiz
	var defaultQuizName = "QuizName"

	protected fun createTestQuiz(
			name: String? = defaultQuizName,
			questions: MutableList<Question>? = mutableListOf(createTestQuestion()),
			id: Long? = null
	): Quiz {
		return quizRepo.save(Quiz(name, questions, id))
	}
}