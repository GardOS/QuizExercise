package no.gardos.gateway.api

import io.swagger.annotations.Api
import no.gardos.gateway.model.UserService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@Api(description = "API for authentication.")
@RestController
@Validated
class UserController(
		private val service: UserService,
		private val authenticationManager: AuthenticationManager,
		private val userDetailsService: UserDetailsService
) {
	@RequestMapping("/user")
	fun user(user: Principal): ResponseEntity<Map<String, Any>> {
		return ResponseEntity.ok().build() //TODO: Functionality
	}

	@PostMapping(path = ["/signIn"], consumes = [(MediaType.APPLICATION_FORM_URLENCODED_VALUE)])
	fun signIn(@ModelAttribute(name = "username") username: String,
	           @ModelAttribute(name = "password") password: String
	): ResponseEntity<Void> {
		return ResponseEntity.ok().build() //TODO: Functionality
	}
}