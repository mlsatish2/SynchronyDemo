package com.synchrony.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.synchrony.demo.models.User;
@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	
	Optional<User> findByEmail(String email);
	
	@Modifying
	@Transactional
	@Query(value =  "ALTER TABLE users AUTO_INCREMENT = 1", nativeQuery = true)
	void resetAutoIncrement();

}
