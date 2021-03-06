package com.user.account.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.websocket.server.PathParam;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.user.account.model.User;
import com.user.account.service.UserService;


@RestController
@RequestMapping("user")
@CrossOrigin
public class UserController {
	
	private static final Logger logger = LogManager.getLogger(UserController.class);
	
	@Autowired
	UserService userService;
	
	@GetMapping("/getAll")
	public ResponseEntity<List<User>> getAllUsers(){
		logger.info("UserController getAll called.");
		List<User> result = userService.getAllUserList();
		logger.info("UserController getAll response: {}", result.toString());
		return ResponseEntity.ok(result);
	}
	
	@GetMapping("/getAllByPage/{pageSize}/{pageNo}")
	public ResponseEntity<Page<User>> getAllByPage(@PathVariable int pageSize, @PathVariable int pageNo){
		logger.info("UserController getAllByPage called. pageSize: {} , pageNo: {} ",pageSize, pageNo);
		Page<User> result = userService.getAllUserPage(pageNo, pageSize);
		logger.info("UserController getAllByPage response: {} : {}", result, result.getContent());
		return ResponseEntity.ok(result);
	}
	
	@PostMapping("/createUpdate")
	public ResponseEntity<List<User>> createUpdateUser(@Valid @RequestBody User user){
		logger.info("UserController createUpdateUser called. user: {}", user != null ? user.toString() : null);
		List<User> result = userService.createUpdateUser(user);
		logger.info("UserController createUpdateUser response: {}", result);
		return ResponseEntity.ok(result);
	}
	
	@PostMapping("/delete/{id}")
	public ResponseEntity<List<User>> deleteUserById(@PathVariable Long id){
		logger.info("UserController deleteUserById called. id: {}", id);
		User deleteUser = userService.getUserById(id);
		if(deleteUser != null) {
			userService.deleteUserById(id);
			List<User> users = userService.getAllUserList();
			logger.info("UserController deleteUserById response: {}", users);
			return ResponseEntity.ok(users);
		}else {
			List<User> users = new ArrayList<User>();
			User errorUser = new User();
			List<String> errors = new ArrayList<String>();
			errors.add("Unable to find user id: " + id);
			errorUser.setErrors(errors);
			users.add(errorUser);
			logger.info("UserController deleteUserById response: {}", users);
			return ResponseEntity.ok(users);
		}
		
	}
}
