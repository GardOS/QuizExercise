package no.gardos.gateway

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import javax.sql.DataSource

@Configuration
@EnableWebSecurity
class WebSecurityConfig(
		private val dataSource: DataSource,
		private val passwordEncoder: PasswordEncoder
) : WebSecurityConfigurerAdapter() {

	@Bean
	override fun userDetailsServiceBean(): UserDetailsService {
		return super.userDetailsServiceBean()
	}

	override fun configure(http: HttpSecurity) {
		http.httpBasic()
				.and()
				.logout()
				.and()
				.authorizeRequests()
				//Required for Swagger-ui
				.antMatchers("/swagger-resources/**", "/webjars/**", "/swagger-ui.html").permitAll()
				.antMatchers("/signIn").permitAll()
				.antMatchers("/user").authenticated()
				.antMatchers("/quiz-server/**").authenticated()
				.antMatchers("/game-server/**").authenticated() //Todo: Admin?
				.anyRequest().denyAll()
				.and()
				.csrf()
				.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
	}

	//TODO: Understand this
	override fun configure(auth: AuthenticationManagerBuilder) {
		auth.jdbcAuthentication()
				.dataSource(dataSource)
				.usersByUsernameQuery("SELECT username, password, enabled FROM users WHERE username=?")
				.authoritiesByUsernameQuery(
						"SELECT x.username, y.roles FROM users x, user_roles y " +
								"WHERE x.username=? and y.user_username=x.username")
				.passwordEncoder(passwordEncoder)
	}
}