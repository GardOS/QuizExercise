package no.gardos.player.model.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class Player(
		@get:Id @get:GeneratedValue
		var id: Long? = null
)