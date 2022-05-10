package com.user.account.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.user.account.model.User;
import com.user.account.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
	
	@Mock
	UserRepository userRepository;
	
	@InjectMocks
	UserServiceImpl userServiceImpl;
	
	@Test
	void getAllUserList() {
		List<User> returnUsers = new ArrayList<User>();
		User user = new User();
		user.setId(1l);
		user.setFirstName("first name");
		user.setLastName("last name");
		user.setEmail("test@email.com");
		returnUsers.add(user);
		
		Mockito.when(userRepository.findAll()).thenReturn(returnUsers);
		userServiceImpl.getAllUserList();
		Mockito.verify(userRepository, Mockito.times(1)).findAll();
	}

	@ParameterizedTest
	@CsvSource({
		//test user input null
		",,,,,false,false,true",
		//test new user
		",first name, last name, test@email.com,,false,false,true,false",
		//test existing user update same email
		"1,first name, last name, test@email.com, test@email.com,true,false,true,false",
		//test existing user update different email and no existing email
		"1,first name, last name, test@email.com, test1@email.com,true,true,false,false",
		//test existing user update different email and existing email
		"1,first name, last name, test@email.com, test1@email.com,true,true,true,false",
		//test update existing user with unable to find by id
		"1,first name, last name, test@email.com, test1@email.com,true,false,true,true",
	})
	void createUpdateUser(ArgumentsAccessor args) {
		User user = null;
		User returnUser = null;
		Optional<User> returnUserValue = null;
		List<User> returnUsers = null;
		
		if(StringUtils.isNotBlank(args.getString(0)) || StringUtils.isNotBlank(args.getString(1))) {
			user = new User();
			user.setId(args.getLong(0));
			user.setFirstName(args.getString(1));
			user.setLastName(args.getString(2));
			user.setEmail(args.getString(3));
			
			returnUser = new User();
			returnUser.setEmail(args.getString(4));
			if(args.getBoolean(8)) {
				returnUserValue = Optional.empty();
			}else {
				returnUserValue = Optional.of((User) returnUser);
			}
			
			
			returnUsers = new ArrayList<User>();
			if(!args.getBoolean(7)) {
				returnUsers.add(returnUser);
			}
		}
		
		if(args.getBoolean(5)) {
			Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(returnUserValue);
		}
		
		if(args.getBoolean(6)){
			Mockito.when(userRepository.getUsersByEmail(Mockito.anyString())).thenReturn(returnUsers);
		}
		
		List<User> users = userServiceImpl.createUpdateUser(user);
		if((StringUtils.isBlank(args.getString(0)) && StringUtils.isBlank(args.getString(1))) ||
				!args.getBoolean(7) || args.getBoolean(8)) {
			assertEquals(users.get(0).getErrors().size(), 1);
		}else {
			Mockito.verify(userRepository, Mockito.times(1)).save(user);
		}
	}
	
	@ParameterizedTest
	@CsvSource({
		//test found user by id
		"1,false",
		//test not found user by id
		",true",
	})
	void getUserById(ArgumentsAccessor args) {
		User user = new User();
		user.setId(1l);
		user.setFirstName("first name");
		user.setLastName("last name");
		user.setEmail("test@email.com");
		
		Optional<User> returnUserValue = Optional.of((User) user);
		Optional<User> returnUserValue2 = Optional.empty();
		
		if(!args.getBoolean(1)) {
			Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(returnUserValue);
		}else {
			Mockito.when(userRepository.findById(Mockito.anyLong())).thenReturn(returnUserValue2);
		}
		
		User result = userServiceImpl.getUserById(1l);
		assertEquals(result == null, args.getBoolean(1));
	}
	
	@Test
	void deleteUserById() {
		Mockito.doNothing().when(userRepository).deleteById(Mockito.anyLong());
		userServiceImpl.deleteUserById(1l);
		Mockito.verify(userRepository, Mockito.times(1)).deleteById(Mockito.anyLong());
	}

}
