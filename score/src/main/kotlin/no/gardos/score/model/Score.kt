package no.gardos.score.model

import org.jetbrains.annotations.NotNull
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class Score(
		@get:NotNull
		var quiz: Long? = null,
		@get:NotNull
		var player: String? = null,
		@get:NotNull
		var score: Int = 0,
		@get:Id @get:GeneratedValue
		var id: Long? = null
)