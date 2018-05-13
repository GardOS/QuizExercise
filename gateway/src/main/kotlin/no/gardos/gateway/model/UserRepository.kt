package no.gardos.gateway.model

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import javax.transaction.Transactional

@Repository
interface UserRepository : JpaRepository<User, Long>, UserRepositoryCustom

@Transactional
interface UserRepositoryCustom

