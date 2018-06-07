package no.gardos.score.model

import no.gardos.schema.ScoreDto

class ScoreConverter {
	companion object {
		fun transform(score: Score): ScoreDto {
			return ScoreDto(
					quiz = score.quiz,
					player = score.player,
					score = score.score,
					id = score.id
			)
		}

		fun transform(categories: Iterable<Score>): List<ScoreDto> {
			return categories.map { transform(it) }
		}
	}
}