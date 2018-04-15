package no.gardos.question

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import no.gardos.quiz.QuizApplication
import no.gardos.schema.CategoryDto
import no.gardos.schema.QuestionDto
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.boot.context.embedded.LocalServerPort
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [(QuizApplication::class)])
abstract class ApiTestBase {

	@LocalServerPort
	protected var port = 0

	@Before
	fun clean() {
		RestAssured.baseURI = "http://localhost"
		RestAssured.basePath = "/quizexercise/api"
		RestAssured.port = port
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
	}
}