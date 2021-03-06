package com.user.account.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.user.account.model.User;
import com.user.account.repository.UserRepository;

@Service("UserService")
public class UserServiceImpl implements UserService{
	
	private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	RestTemplate restTemplate;
	
	final static String BLACKLIST_USER_URL = "http://localhost:8081/blacklist-user/checkBlacklistUser";
	
	final static String BLACKLIST_CALL_ERROR = "Unable to call Blacklist API! Please try again later.";
	
	final static String BLACKLIST_ERROR = "This user is blacklisted! First Name: {0}, Last Name: {1}";
	
	@Override
	public List<User> getAllUserList() {
		return userRepository.findAll();
	}
	
	@Override
	public Page<User> getAllUserPage(int pageNo, int pageSize) {
		boolean result = validatePage(pageNo, pageSize);
		if(result) {
			List<User> errorUsers = new ArrayList<User>();
			User errorUser = new User();
			List<String> errors = new ArrayList<String>();
			errors.add("pageNo and pageSize must larger than 0! pageNo: "+ pageNo + " pageSize: " + pageSize);
			errorUser.setErrors(errors);
			errorUsers.add(errorUser);
			Page<User> errorPage = new PageImpl<User>(errorUsers); 
			
			return errorPage;
		}else {
			Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
			return userRepository.findAll(pageable);
		}
	}
	
	private boolean validatePage(int pageNo, int pageSize) {
		if(pageNo < 1 || pageSize < 1) {
			return true;
		}
		
		return false;
	}

	@Override
	@Transactional
	public List<User> createUpdateUser(User user) {
		if(user != null && user.getId() != null) {
			return processExistingUser(user);
		}else if(user != null && user.getId() == null) {
			//Check New User 
			return checkSaveUser(user);
		}else {
			// User object null return error
			List<User> errorUsers = new ArrayList<User>();
			User errorUser = new User();
			List<String> errors = new ArrayList<String>();
			errors.add("User input cannot be empty!");
			errorUser.setErrors(errors);
			errorUsers.add(errorUser);
			
			return errorUsers;
		}
	}
	
	private List<User> processExistingUser(User user) {
		//Check Existing User
		Optional<User> existingUserOp = userRepository.findById(user.getId());
		if(existingUserOp.isPresent()) {
			User existingUser = existingUserOp.get();
			user.setVersion(existingUser.getVersion());
			if(existingUser.getEmail().equals(user.getEmail())) {
				//Same email no need check
				userRepository.save(user);
				return userRepository.findAll();
			}else {
				//updated email need check
				return checkSaveUser(user);
			}
		}else {
			// User object null return error
			List<User> errorUsers = new ArrayList<User>();
			User errorUser = new User();
			List<String> errors = new ArrayList<String>();
			errors.add("Unable to find User Id! Id: "+ user.getId());
			errorUser.setErrors(errors);
			errorUsers.add(errorUser);
			
			return errorUsers;
		}
	}
	
	private List<User> checkSaveUser(User user){
		// Business validation
		// Check existing email
		List<String> errorList = validateEmail(user);
		// check blacklist user
		errorList.addAll(checkBlacklistUser(user));
		
		if(CollectionUtils.isEmpty(errorList)) {
			//No Error
			userRepository.save(user);
			return userRepository.findAll();
		}else {
			//Set Error
			List<User> errorUsers = new ArrayList<User>();
			user.setErrors(errorList);
			errorUsers.add(user);
			return errorUsers;
		}
	}
	
	private List<String> checkBlacklistUser(User user){
		List<String> errors = new ArrayList<String>();
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.ALLOW, MediaType.APPLICATION_JSON_VALUE);
			headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(BLACKLIST_USER_URL);
			
			ResponseEntity<Boolean> responseObject = restTemplate.exchange(
					uriBuilder.build().encode().toUri(), 
					HttpMethod.POST, 
					new HttpEntity<User>(user,headers), 
					new ParameterizedTypeReference<Boolean>() {
			});
			
			if((responseObject.getStatusCode() == HttpStatus.OK) && (responseObject.getBody() != null)) {
				Boolean response = responseObject.getBody();
				if(response) {
					//Is blacklisted user
					String errorMsg = MessageFormat.format(BLACKLIST_ERROR, user.getFirstName(), user.getLastName());
					errors.add(errorMsg);
				}
			}
		}catch (RestClientException e) {
			// Error call to blacklist user api
			logger.error(e);
			errors.add(BLACKLIST_CALL_ERROR);
		}
		
		return errors;
	}
	
	private List<String> validateEmail(User user){
		List<String> errors = new ArrayList<String>();
		List<User> users = userRepository.getUsersByEmail(user.getEmail());
		if(!CollectionUtils.isEmpty(users)) {
			errors.add("Email address already used!");
		}
		
		return errors;
	}

	@Override
	public User getUserById(Long id) {
		Optional<User> optionalUser = userRepository.findById(id);
		if(!optionalUser.isEmpty()) {
			return optionalUser.get();
		}
		
		return null;
	}

	@Override
	@Transactional
	public void deleteUserById(Long id) {
		userRepository.deleteById(id);
	}

}
