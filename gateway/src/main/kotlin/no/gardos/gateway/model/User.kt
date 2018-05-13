package no.gardos.gateway.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class User(
		@get:Id @get:GeneratedValue
		var id: Long? = null
)