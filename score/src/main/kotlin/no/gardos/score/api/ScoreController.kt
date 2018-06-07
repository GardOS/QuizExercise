package no.gardos.score.api

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import no.gardos.schema.GameStateDto
import no.gardos.schema.ScoreDto
import no.gardos.score.model.Score
import no.gardos.score.model.ScoreConverter
import no.gardos.score.model.ScoreRepository
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Api(value = "/scores", description = "API for scores.")
@RequestMapping(
		path = ["/scores"],
		produces = [(MediaType.APPLICATION_JSON_VALUE)]
)
@RestController
@Validated
class ScoreController {
	@Autowired
	private lateinit var scoreRepo: ScoreRepository

	@RabbitListener(queues = ["#{queue.name}"]) //TODO: How does #queue.name work?
	fun saveScore(gameState: GameStateDto) {
		try {
			val score = Score(quiz = gameState.quiz, player = gameState.id, score = gameState.Score)
			scoreRepo.save(score)
		} catch (e: Exception) {
		}
	}

	@ApiOperation("Get all the scores")
	@GetMapping
	fun getScores(): ResponseEntity<List<ScoreDto>> {
		return ResponseEntity.ok(ScoreConverter.transform(scoreRepo.findAll()))
	}
}