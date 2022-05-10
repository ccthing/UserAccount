package com.user.account.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.user.account.model.User;
import com.user.account.repository.UserRepository;
import com.user.account.service.UserService;
import com.user.account.service.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
	
	@Mock
	UserService userService = new UserServiceImpl();
	
	@Mock
	UserRepository userRepository;
	
	@InjectMocks
	UserController userController;
	
	
	
	@Test
	void getAllUsers() {
		List<User> returnUsers = new ArrayList<User>();
		User user = new User();
		user.setId(1l);
		user.setFirstName("first name");
		user.setLastName("last name");
		user.setEmail("test@email.com");
		returnUsers.add(user);
		
		Mockito.when(userService.getAllUserList()).thenReturn(returnUsers);
		ResponseEntity<List<User>> result = userController.getAllUsers();
		
		assertEquals(result.getBody().size(), 1);
	}
	
	@Test
	void createUpdateUser() {
		List<User> returnUsers = new ArrayList<User>();
		User user = new User();
		user.setId(1l);
		user.setFirstName("first name");
		user.setLastName("last name");
		user.setEmail("test@email.com");
		returnUsers.add(user);
		
		Mockito.when(userService.createUpdateUser(Mockito.any())).thenReturn(returnUsers);
		ResponseEntity<List<User>> result = userController.createUpdateUser(user);
		
		assertEquals(result.getBody().size(), 1);
	}
	
	@ParameterizedTest
	@CsvSource({
		//test found user by id
		"1,true",
		//test not found user by id
		"2,false",
	})
	void deleteUserById(ArgumentsAccessor args) {
		List<User> returnUsers = new ArrayList<User>();
		User user = new User();
		user.setId(1l);
		user.setFirstName("first name");
		user.setLastName("last name");
		user.setEmail("test@email.com");
		returnUsers.add(user);
		
		if(args.getBoolean(1)) {
			Mockito.when(userService.getUserById(Mockito.anyLong())).thenReturn(user);
			Mockito.when(userService.getAllUserList()).thenReturn(returnUsers);
		}else {
			Mockito.when(userService.getUserById(Mockito.anyLong())).thenReturn(null);
		}
		
		ResponseEntity<List<User>> result = userController.deleteUserById(args.getLong(0));
		
		assertEquals(CollectionUtils.isEmpty(result.getBody().get(0).getErrors()), args.getBoolean(1));
	}

}
