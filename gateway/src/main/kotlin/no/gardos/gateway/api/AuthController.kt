package no.gardos.gateway.api

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import no.gardos.gateway.model.UserService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.security.Principal

@Api(description = "API for authentication.")
@RestController
@Validated
class AuthController(
		private val service: UserService,
		private val authenticationManager: AuthenticationManager,
		private val userDetailsService: UserDetailsService
) {
	//TODO: remove
	@ApiOperation("Retrieves currently logged on user")
	@GetMapping(path = ["/username"])
	fun getUsername(user: Principal): ResponseEntity<Any> {
		return ResponseEntity.ok().body(user.name)
	}

	@RequestMapping("/user")
	fun user(user: Principal): ResponseEntity<Map<String, Any>> {
		val map = mutableMapOf<String, Any>()
		map["name"] = user.name
		map["roles"] = AuthorityUtils.authorityListToSet((user as Authentication).authorities)
		return ResponseEntity.ok(map)
	}

	//TODO: Why "the_user", "the_password"?
	@PostMapping(path = ["/signIn"], consumes = [(MediaType.APPLICATION_FORM_URLENCODED_VALUE)])
	fun signIn(@ModelAttribute(name = "the_user") username: String,
	           @ModelAttribute(name = "the_password") password: String
	): ResponseEntity<Void> {
		val registered = service.createUser(username, password, setOf("USER"))

		if (!registered) {
			return ResponseEntity.status(400).build()
		}

		val userDetails = userDetailsService.loadUserByUsername(username)
		val token = UsernamePasswordAuthenticationToken(userDetails, password, userDetails.authorities)

		authenticationManager.authenticate(token)

		if (token.isAuthenticated) {
			SecurityContextHolder.getContext().authentication = token
		}

		return ResponseEntity.status(204).build()
	}
}