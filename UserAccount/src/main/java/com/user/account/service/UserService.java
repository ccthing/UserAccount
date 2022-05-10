package com.user.account.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.user.account.model.User;

public interface UserService {
	List<User> getAllUserList();
	List<User> createUpdateUser(User user);
	User getUserById(Long id);
	void deleteUserById(Long id);
	Page<User> getAllUserPage(int pageNo, int pageSize);
}
