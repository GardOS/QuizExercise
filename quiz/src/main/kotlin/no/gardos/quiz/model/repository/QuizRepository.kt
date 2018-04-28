package no.gardos.quiz.model.repository

import no.gardos.quiz.model.entity.Quiz
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.transaction.Transactional

@Repository
interface QuizRepository : CrudRepository<Quiz, Long>, QuizRepositoryCustom {
	fun findByName(name: String): Quiz?
}

@Transactional
interface QuizRepositoryCustom

