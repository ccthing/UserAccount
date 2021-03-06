package com.user.account.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.user.account.model.User;

@Repository("UserRepository")
public interface UserRepository extends JpaRepository<User, Long>{
	
	@Query("SELECT u FROM User u where u.firstName = :firstName")
	public List<User> getUsersByFirstName(@Param("firstName") String firstName);
	
	@Query("SELECT u FROM User u where u.email = :email")
	public List<User> getUsersByEmail(@Param("email") String email);
	
}
