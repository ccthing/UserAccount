package com.blacklist.user.account.controller;

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

import com.blacklist.user.account.model.BlacklistUser;
import com.blacklist.user.account.service.BlacklistUserService;


@RestController
@RequestMapping("blacklist-user")
@CrossOrigin
public class BlacklistUserController {
	
	private static final Logger logger = LogManager.getLogger(BlacklistUserController.class);
	
	@Autowired
	BlacklistUserService blacklistUserService;
	
	@GetMapping("/getAll")
	public ResponseEntity<List<BlacklistUser>> getAllUsers(){
		logger.info("BlacklistUserController getAll called.");
		List<BlacklistUser> result = blacklistUserService.getAllBlacklistUserList();
		logger.info("BlacklistUserController getAll response: {}", result.toString());
		return ResponseEntity.ok(result);
	}
	
	@GetMapping("/getAllByPage/{pageSize}/{pageNo}")
	public ResponseEntity<Page<BlacklistUser>> getAllByPage(@PathVariable int pageSize, @PathVariable int pageNo){
		logger.info("BlacklistUserController getAllByPage called. pageSize: {} , pageNo: {} ",pageSize, pageNo);
		Page<BlacklistUser> result = blacklistUserService.getAllBlacklistUserPage(pageNo, pageSize);
		logger.info("BlacklistUserController getAllByPage response: {} : {}", result, result.getContent());
		return ResponseEntity.ok(result);
	}
	
	@PostMapping("/createUpdate")
	public ResponseEntity<List<BlacklistUser>> createUpdateBlacklistUser(@Valid @RequestBody BlacklistUser user){
		logger.info("BlacklistUserController createUpdateUser called. user: {}", user != null ? user.toString() : null);
		List<BlacklistUser> result = blacklistUserService.createUpdateBlacklistUser(user);
		logger.info("BlacklistUserController createUpdateUser response: {}", result);
		return ResponseEntity.ok(result);
	}
	
	@PostMapping("/checkBlacklistUser")
	public ResponseEntity<Boolean> checkBlacklistUser(@Valid @RequestBody BlacklistUser user){
		logger.info("BlacklistUserController checkBlacklistUser called. user: {}", user != null ? user.toString() : null);
		Boolean result = blacklistUserService.checkBlacklistUser(user);
		logger.info("BlacklistUserController checkBlacklistUser response: {}", result);
		return ResponseEntity.ok(result);
	}
	
	@PostMapping("/delete/{id}")
	public ResponseEntity<List<BlacklistUser>> deleteUserById(@PathVariable Long id){
		logger.info("BlacklistUserController deleteUserById called. id: {}", id);
		BlacklistUser deleteUser = blacklistUserService.getBlacklistUserById(id);
		if(deleteUser != null) {
			blacklistUserService.deleteBlacklistUserById(id);
			List<BlacklistUser> users = blacklistUserService.getAllBlacklistUserList();
			logger.info("BlacklistUserController deleteUserById response: {}", users);
			return ResponseEntity.ok(users);
		}else {
			List<BlacklistUser> users = new ArrayList<BlacklistUser>();
			BlacklistUser errorUser = new BlacklistUser();
			List<String> errors = new ArrayList<String>();
			errors.add("Unable to find user id: " + id);
			errorUser.setErrors(errors);
			users.add(errorUser);
			logger.info("BlacklistUserController deleteUserById response: {}", users);
			return ResponseEntity.ok(users);
		}
		
	}
}
