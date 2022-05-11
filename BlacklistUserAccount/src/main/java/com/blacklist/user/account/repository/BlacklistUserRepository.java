package com.blacklist.user.account.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.blacklist.user.account.model.BlacklistUser;

@Repository("BlacklistUserRepository")
public interface BlacklistUserRepository extends JpaRepository<BlacklistUser, Long>{
	
	@Query("SELECT u FROM BlacklistUser u where u.firstName = :firstName AND u.lastName = :lastName")
	public List<BlacklistUser> getUsersByName(@Param("firstName") String firstName, @Param("lastName") String lastName);
	@Query("SELECT u FROM BlacklistUser u where u.firstName = :firstName AND u.lastName = :lastName AND u.active = 'true'")
	public List<BlacklistUser> getActiveUsersByName(@Param("firstName") String firstName, @Param("lastName") String lastName);
	
}
