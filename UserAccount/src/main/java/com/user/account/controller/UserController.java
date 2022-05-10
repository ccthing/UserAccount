package com.user.account.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.websocket.server.PathParam;

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
	
	@Autowired
	UserService userService;
	
	final static int PAGE_SIZE = 10;
	
	@GetMapping("/getAll")
	public ResponseEntity<List<User>> getAllUsers(){
		return ResponseEntity.ok(userService.getAllUserList());
	}
	
	@GetMapping("/getAllByPage/{pageNo}")
	public ResponseEntity<Page<User>> getAllByPage(@PathVariable int pageNo){
		return ResponseEntity.ok(userService.getAllUserPage(pageNo, PAGE_SIZE));
	}
	
	@PostMapping("/createUpdate")
	public ResponseEntity<List<User>> createUpdateUser(@Valid @RequestBody User user){
		return ResponseEntity.ok(userService.createUpdateUser(user));
	}
	
	@PostMapping("/delete/{id}")
	public ResponseEntity<List<User>> deleteUserById(@PathVariable Long id){
		User deleteUser = userService.getUserById(id);
		if(deleteUser != null) {
			userService.deleteUserById(id);
			List<User> users = userService.getAllUserList();
			return ResponseEntity.ok(users);
		}else {
			List<User> users = new ArrayList<User>();
			User errorUser = new User();
			List<String> errors = new ArrayList<String>();
			errors.add("Unable to find user id: " + id);
			errorUser.setErrors(errors);
			users.add(errorUser);
			return ResponseEntity.ok(users);
		}
		
	}
}