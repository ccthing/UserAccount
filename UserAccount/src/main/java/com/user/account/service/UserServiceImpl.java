package com.user.account.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.user.account.model.User;
import com.user.account.repository.UserRepository;

@Service("UserService")
public class UserServiceImpl implements UserService{
	
	@Autowired
	UserRepository userRepository;
	
	@Override
	public List<User> getAllUserList() {
		return userRepository.findAll();
	}
	
	@Override
	public Page<User> getAllUserPage(int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
		return userRepository.findAll(pageable);
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
		List<String> errorList = validateEmail(user);
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