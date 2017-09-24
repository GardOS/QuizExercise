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

    @Test
    fun testInit(){
        assertNotNull(quizCrud)
    }

    @Test
    fun crud(){
        assertEquals(0, quizCrud.count())
        val quiz = QuizEntity("hei")
        val id = quizCrud.save(quiz).id
        assertEquals(1, quizCrud.count())
        assertEquals("hei", quizCrud.findOne(id).questionText)
    }
}

@SpringBootApplication
class TestApplication