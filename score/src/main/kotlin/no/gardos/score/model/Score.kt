package no.gardos.score.model

import org.jetbrains.annotations.NotNull
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class Score(
		@get:NotNull
		var Quiz: Long? = null,
		@get:NotNull
		var Player: String? = null,
		@get:NotNull
		var Score: Int = 0,
		@get:Id @get:GeneratedValue
		var id: Long? = null
)