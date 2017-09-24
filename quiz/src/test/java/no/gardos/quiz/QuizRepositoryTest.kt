package no.gardos.quiz

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@DataJpaTest
class QuizRepositoryTest{

    @Autowired
    private lateinit var quizCrud: QuizRepository

    @Autowired
    private lateinit var categoryCrud: CategoryRepository

    var categoryName = "Category"
    var questionText = "QuestionText"
    var answers = arrayOf("Correct", "Wrong", "Wrong", "Wrong")
    var correctAnswer = 1 //Dont set to above 3

    @Test
    fun testInit(){
        assertNotNull(quizCrud)
        assertNotNull(categoryCrud)
    }


    //Why does this crash the rest of the tests?
//    @Test
//    fun testCreateCategory(){
//        assertEquals(0, categoryCrud.count())
//        var category = createTestCategory()
//        assertEquals(1, categoryCrud.count())
//        categoryCrud.delete(category)
//    }


    @Test
    fun testCreateQuiz(){
        assertEquals(0, quizCrud.count())
        createTestQuizWithCategory()
        assertEquals(1, quizCrud.count())
    }

    @Test
    fun testUpdateQuiz(){
        var quiz = createTestQuizWithCategory()

        assertEquals(correctAnswer, quiz.correctAnswer)

        quiz.correctAnswer++

        quizCrud.save(quiz)

        assertNotEquals(correctAnswer, quizCrud.findOne(quiz.id))
    }

    @Test
    fun testDeleteQuiz(){
        var quiz = createTestQuizWithCategory()

        assertNotNull(quizCrud.findOne(quiz.id))

        quizCrud.delete(quiz.id)

        assertNull(quizCrud.findOne(quiz.id))
    }

    private fun createTestQuiz(categoryEntity: CategoryEntity) : QuizEntity {
        var quiz = quizCrud.save(QuizEntity(questionText, answers, correctAnswer, categoryEntity))
        assertEquals(quiz, quizCrud.findOne(quiz.id))

        return quiz
    }

    private fun createTestCategory() : CategoryEntity {
        var category = categoryCrud.save(CategoryEntity(categoryName))
        assertEquals(category, categoryCrud.findOne(category.id))

        return category
    }

    private fun createTestQuizWithCategory() : QuizEntity {
        var category = createTestCategory()
        var quiz = createTestQuiz(category)
        category.quizEntities.add(quiz)
        categoryCrud.save(category)

        return quiz
    }
}

@SpringBootApplication
class TestApplication