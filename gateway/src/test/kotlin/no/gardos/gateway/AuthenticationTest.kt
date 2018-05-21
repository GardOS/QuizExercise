package no.gardos.gateway

import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.context.embedded.LocalServerPort
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.EnvironmentTestUtils
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import org.testcontainers.containers.GenericContainer

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = [(AuthenticationTest.Companion.Initializer::class)])
class AuthenticationTest {
	@LocalServerPort
	private var port = 0

	//Note for Windows: only works if Docker for Windows has "Expose daemon" enabled
	companion object {
		class KGenericContainer(imageName: String) : GenericContainer<KGenericContainer>(imageName)

		@ClassRule
		@JvmField
		val redis = KGenericContainer("redis:latest").withExposedPorts(6379)!!

		class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
			override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
				val host = redis.containerIpAddress
				val port = redis.getMappedPort(6379)

				EnvironmentTestUtils.addEnvironment(
						"testcontainers",
						configurableApplicationContext.environment,
						"spring.redis.host=$host",
						"spring.redis.port=$port"
				)
			}
		}
	}

	@Before
	fun initialize() {
		RestAssured.baseURI = "http://localhost"
		RestAssured.port = port
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
	}

	@Test
	fun testUnauthorizedAccess() {
		RestAssured.given().get("/user")
				.then()
				.statusCode(401)
	}

	@Test
	fun testAuthorizedAccess() {
		val username = "username"
		val password = "password"

		val token = RestAssured.given().contentType(ContentType.URLENC)
				.formParam("username", username)
				.formParam("password", password)
				.post("/signIn")
				.then()
				.statusCode(403)
				.extract().cookie("XSRF-TOKEN")

		val session = RestAssured.given().contentType(ContentType.URLENC)
				.formParam("username", username)
				.formParam("password", password)
				.header("X-XSRF-TOKEN", token)
				.cookie("XSRF-TOKEN", token)
				.post("/signIn")
				.then()
				.statusCode(204)
				.extract().cookie("SESSION")

		RestAssured.given().cookie("SESSION", Pair(session, token).first)
				.get("/user")
				.then()
				.statusCode(200)
				.body("name", CoreMatchers.equalTo(username))
				.body("roles", Matchers.contains("ROLE_USER"))
	}
}