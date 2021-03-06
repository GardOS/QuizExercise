package no.gardos.quiz.model

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import javax.transaction.Transactional

@Repository
interface QuizRepository : JpaRepository<Quiz, Long>, QuizRepositoryCustom {
	fun findByName(name: String): Quiz?
}

@Transactional
interface QuizRepositoryCustom

