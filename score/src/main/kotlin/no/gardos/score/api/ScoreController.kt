package no.gardos.score.api

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import no.gardos.schema.ScoreDto
import no.gardos.score.model.Score
import no.gardos.score.model.ScoreConverter
import no.gardos.score.model.ScoreRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Api(value = "/scores", description = "API for scores.")
@RequestMapping(
		path = ["/scores"],
		produces = [(MediaType.APPLICATION_JSON_VALUE)]
)
@RestController
@Validated
class CategoryController {
	@Autowired
	private lateinit var scoreRepo: ScoreRepository

	@ApiOperation("Get all the scores")
	@GetMapping
	fun getScores(): ResponseEntity<List<ScoreDto>> {
		return ResponseEntity.ok(ScoreConverter.transform(scoreRepo.findAll()))
	}

	@ApiOperation("Create score")
	@PostMapping
	fun createScore(): ResponseEntity<Any> {
		val score = Score(quiz = 0, player = "player", score = 0)
		scoreRepo.save(score)
		return ResponseEntity.noContent().build()
	}
}