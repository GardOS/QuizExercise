package no.gardos.game.api

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import no.gardos.game.model.GameState
import no.gardos.game.model.GameStateConverter
import no.gardos.game.model.GameStateRepository
import no.gardos.schema.GameStateDto
import no.gardos.schema.QuestionDto
import no.gardos.schema.QuizDto
import org.springframework.amqp.core.FanoutExchange
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.*
import org.springframework.transaction.TransactionSystemException
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import java.security.Principal
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession
import javax.validation.ConstraintViolationException

@Api(value = "/games", description = "API for interacting with the game.")
@RequestMapping(
		path = ["/games"],
		produces = [(MediaType.APPLICATION_JSON_VALUE)]
)
@RestController
@Validated
class GameController {
	@Autowired
	private lateinit var restTemplate: RestTemplate

	@Autowired
	private lateinit var gameStateRepo: GameStateRepository

	@Value("\${quizServerPath}")
	private lateinit var quizServerPath: String

	@Autowired
	private lateinit var template: RabbitTemplate

	@Autowired
	private lateinit var fanout: FanoutExchange

	@ApiOperation("Get a game by ID")
	@GetMapping(path = ["/{id}"])
	fun getGame(
			@ApiParam("Id of the game")
			@PathVariable("id")
			pathId: Long
	): ResponseEntity<Any> {
		val game = gameStateRepo.findOne(pathId)
				?: return ResponseEntity.status(404).body("Game with id: $pathId not found")

		return ResponseEntity.ok(game)
	}

	@ApiOperation("Starts a new game. Initializes a GameState object for tracking the game")
	@PostMapping(path = ["/new-game"])
	fun newGame(
			@ApiParam
			@RequestBody
			dto: GameStateDto,
			user: Principal,
			session: HttpSession
	): ResponseEntity<Any> {
		if (dto.id != null) {
			return ResponseEntity.status(400).body("Id should not be specified")
		}

		if (dto.quiz == null) {
			return ResponseEntity.status(400).body("Invalid request. References to quiz invalid")
		}

		if (user.name == null)
			return ResponseEntity.status(400).body("Currently logged on user is invalid")

		if (session.id == null)
			ResponseEntity.status(403).body("Invalid session")

		val url = "$quizServerPath/${dto.quiz}"
		val headers = HttpHeaders()
		headers.add("cookie", "SESSION=${session.id}")
		val httpEntity = HttpEntity(null, headers)

		val quiz: QuizDto?
		try {
			quiz = restTemplate.exchange(url, HttpMethod.GET, httpEntity, QuizDto::class.java).body
		} catch (ex: HttpClientErrorException) {
			return ResponseEntity.status(ex.statusCode).body("Error when querying Quiz:\n ${ex.responseBodyAsString}")
		}

		val gameState = gameStateRepo.save(
				GameState(
						Quiz = quiz!!.id!!.toLong(),
						Player = user.name
				)
		)

		return ResponseEntity.ok(GameStateConverter.transform(gameState))
	}

	@ApiOperation("Get current question")
	@GetMapping(path = ["/{id}/current-question"])
	fun currentQuestion(
			@ApiParam("Id of the game")
			@PathVariable("id")
			pathId: Long,
			session: HttpSession
	): ResponseEntity<Any> {
		val game = gameStateRepo.findOne(pathId)
				?: return ResponseEntity.status(404).body("Game with id: $pathId not found")

		if (game.isFinished) return ResponseEntity.ok().body(game)


		if (session.id == null)
			ResponseEntity.status(403).body("Invalid session")

		val url = "$quizServerPath/${game.Quiz}"
		val headers = HttpHeaders()
		headers.add("cookie", "SESSION=${session.id}")
		val httpEntity = HttpEntity(null, headers)

		val question: QuestionDto
		try {
			val quiz = restTemplate.exchange(url, HttpMethod.GET, httpEntity, QuizDto::class.java).body
			question = quiz?.questions?.getOrNull(game.RoundNumber)
					?: return ResponseEntity.status(500).body("Could not find question")
		} catch (ex: HttpClientErrorException) {
			return ResponseEntity.status(ex.statusCode).body(ex.responseBodyAsString)
		}

		return ResponseEntity.ok(question)
	}


	@ApiOperation("Guess answer on the current question in the game session")
	@PatchMapping(path = ["/{id}"])
	fun guess(
			@ApiParam("Id of the game")
			@PathVariable("id")
			pathId: Long,
			@RequestParam("answer", required = true)
			answer: Int?,
			user: Principal,
			session: HttpSession
	): ResponseEntity<Any> {
		if (answer == null || answer !in 0..3) {
			return ResponseEntity.status(400).body("Invalid parameters")
		}

		if (user.name == null)
			return ResponseEntity.status(400).body("Currently logged on user is invalid")

		val game = gameStateRepo.findOne(pathId)
				?: return ResponseEntity.status(404).body("Game with id: $pathId not found")

		if (game.Player != user.name)
			return ResponseEntity.status(403).body("Currently logged on user did not initiate this game")

		if (game.isFinished) return ResponseEntity.noContent().build()

		if (session.id == null)
			ResponseEntity.status(403).body("Invalid session")

		val url = "$quizServerPath/${game.Quiz}"
		val headers = HttpHeaders()
		headers.add("cookie", "SESSION=${session.id}")
		val httpEntity = HttpEntity(null, headers)

		val quiz: QuizDto?
		try {
			quiz = restTemplate.exchange(url, HttpMethod.GET, httpEntity, QuizDto::class.java).body
		} catch (ex: HttpClientErrorException) {
			game.RoundNumber++ //Instead of being stuck on this question, skip it
			gameStateRepo.save(game)
			return ResponseEntity.status(ex.statusCode).body(ex.responseBodyAsString)
		}

		val correctAnswer = quiz?.questions?.getOrNull(game.RoundNumber)?.correctAnswer

		if (correctAnswer == null) {
			game.RoundNumber++ //Instead of being stuck on this question, skip it
			gameStateRepo.save(game)
			return ResponseEntity.status(500).body("Could not find answer to question")
		}

		val isCorrect = answer == correctAnswer

		game.RoundNumber++
		if (isCorrect) game.Score++
		if (game.RoundNumber >= quiz.questions!!.count()) {
			game.isFinished = true

			template.convertAndSend(fanout.name, "", GameStateConverter.transform(game))
		}

		gameStateRepo.save(game)

		return if (isCorrect) ResponseEntity.ok().body("Correct") else ResponseEntity.ok().body("Wrong")
	}

	@ApiOperation("Easily accessible method for testing rabbitmq")
	@GetMapping(path = ["/rabbitmq"])
	fun rabbitmq(): ResponseEntity<Any> {
		val game = GameStateDto(
				quiz = "1",
				player = "player",
				Score = 1,
				RoundNumber = 2,
				isFinished = true,
				id = "123")
		template.convertAndSend(fanout.name, "", game)
		return ResponseEntity.ok().build()
	}

	//Catches validation errors and returns error status based on error
	@ExceptionHandler(value = ([ConstraintViolationException::class, org.hibernate.exception.ConstraintViolationException::class,
		DataIntegrityViolationException::class, TransactionSystemException::class]))
	fun handleValidationFailure(ex: Exception, response: HttpServletResponse): String {
		var cause: Throwable? = ex
		for (i in 0..4) { //Iterate 5 times max, since it might have infinite depth
			if (cause is ConstraintViolationException || cause is org.hibernate.exception.ConstraintViolationException) {
				response.status = HttpStatus.BAD_REQUEST.value()
				return "Invalid request. Error:\n${ex.message ?: "Error not found"}"
			}
			cause = cause?.cause
		}
		response.status = HttpStatus.INTERNAL_SERVER_ERROR.value()
		return "Something went wrong processing the request.  Error:\n${ex.message ?: "Error not found"}"
	}
}