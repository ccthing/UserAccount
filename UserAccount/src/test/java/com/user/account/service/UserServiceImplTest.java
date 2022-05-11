package com.user.account.service;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.user.account.model.User;
import com.user.account.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
	
	@Mock
	UserRepository userRepository;
	
	@InjectMocks
	UserServiceImpl userServiceImpl;
	
	@Mock
    private RestTemplate restTemplate;

	final static String BLACKLIST_USER_URL = "http://localhost:8081/blacklist-user/checkBlacklistUser";
	
	
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
		//test find all user page
		"10,1,true",
		//test find all user page with error
		"0,1,false",
	})
	void getAllUserPage(ArgumentsAccessor args) {
		List<User> returnUsers = new ArrayList<User>();
		User user = new User();
		user.setId(1l);
		user.setFirstName("first name");
		user.setLastName("last name");
		user.setEmail("test@email.com");
		returnUsers.add(user);
		Page<User> resultPage = new PageImpl<User>(returnUsers); 
		
		if(args.getBoolean(2)) {
			Pageable pageable = PageRequest.of(args.getInteger(0) - 1, args.getInteger(1));
			Mockito.when(userRepository.findAll(pageable)).thenReturn(resultPage);
		}
		
		Page<User> results = userServiceImpl.getAllUserPage(args.getInteger(0), args.getInteger(1));
		if(args.getBoolean(2)) {
			Pageable pageable = PageRequest.of(args.getInteger(0) - 1, args.getInteger(1));
			Mockito.verify(userRepository, Mockito.times(1)).findAll(pageable);
		}else {
			assertTrue(results.getContent().get(0).getErrors().size() > 0);
		}
	}

	@ParameterizedTest
	@CsvSource({
		//test user input null
		",,,,,false,false,true,false,false",
		//test new user
		",first name, last name, test@email.com,,false,false,true,false,true",
		//test existing user update same email
		"1,first name, last name, test@email.com, test@email.com,true,false,true,false,false",
		//test existing user update different email and no existing email and blacklisted user
		"2,first name, last name, test@email.com, test1@email.com,true,true,false,false,true",
		//test existing user update different email and existing email
		"3,first name, last name, test@email.com, test1@email.com,true,true,true,false,true",
		//test update existing user with unable to find by id
		"4,first name, last name, test@email.com, test1@email.com,true,false,true,true,false",
		//test call api fail
		"5,first name, last name, test@email.com, test1@email.com,true,true,false,false,true",
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
		
		if(args.getBoolean(9)){
			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.ALLOW, MediaType.APPLICATION_JSON_VALUE);
			headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(BLACKLIST_USER_URL);
			ResponseEntity<Boolean> myEntity = null;
			if("2".equals(args.getString(0))) {
				myEntity = ResponseEntity.ok(new Boolean(true));
			}else {
				myEntity = ResponseEntity.ok(new Boolean(false));
			}
			
			if(!"5".equals(args.getString(0))) {
				Mockito.when(restTemplate.exchange(
						uriBuilder.build().encode().toUri(), 
						HttpMethod.POST, 
						new HttpEntity<User>(user,headers), 
						new ParameterizedTypeReference<Boolean>() {}
						)).thenReturn(myEntity);
			}else {
				Mockito.when(restTemplate.exchange(
						uriBuilder.build().encode().toUri(), 
						HttpMethod.POST, 
						new HttpEntity<User>(user,headers), 
						new ParameterizedTypeReference<Boolean>() {}
						)).thenThrow(new RestClientException("error"));
			}
		}
		
		List<User> users = userServiceImpl.createUpdateUser(user);
		if((StringUtils.isBlank(args.getString(0)) && StringUtils.isBlank(args.getString(1))) ||
				!args.getBoolean(7) || args.getBoolean(8)) {
			assertTrue(users.get(0).getErrors().size() > 0);
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
