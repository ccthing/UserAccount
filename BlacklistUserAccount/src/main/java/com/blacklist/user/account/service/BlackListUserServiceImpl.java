package com.blacklist.user.account.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.blacklist.user.account.model.BlacklistUser;
import com.blacklist.user.account.repository.BlacklistUserRepository;

@Service("BlackListUserService")
public class BlackListUserServiceImpl implements BlacklistUserService{
	
	@Autowired
	BlacklistUserRepository blacklistUserRepository;
	
	@Override
	public List<BlacklistUser> getAllBlacklistUserList() {
		return blacklistUserRepository.findAll();
	}
	
	@Override
	public Page<BlacklistUser> getAllBlacklistUserPage(int pageNo, int pageSize) {
		boolean result = validatePage(pageNo, pageSize);
		if(result) {
			List<BlacklistUser> errorUsers = new ArrayList<BlacklistUser>();
			BlacklistUser errorUser = new BlacklistUser();
			List<String> errors = new ArrayList<String>();
			errors.add("pageNo and pageSize must larger than 0! pageNo: "+ pageNo + " pageSize: " + pageSize);
			errorUser.setErrors(errors);
			errorUsers.add(errorUser);
			Page<BlacklistUser> errorPage = new PageImpl<BlacklistUser>(errorUsers); 
			
			return errorPage;
		}else {
			Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
			return blacklistUserRepository.findAll(pageable);
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
	public List<BlacklistUser> createUpdateBlacklistUser(BlacklistUser user) {
		if(user != null && user.getId() != null) {
			return processExistingUser(user);
		}else if(user != null && user.getId() == null) {
			//Check New Blacklist User 
			//set active to true if not fillin
			if(StringUtils.isBlank(user.getActive())) {
				user.setActive("true");
			}
			
			return checkSaveUser(user);
		}else {
			// User object null return error
			List<BlacklistUser> errorUsers = new ArrayList<BlacklistUser>();
			BlacklistUser errorUser = new BlacklistUser();
			List<String> errors = new ArrayList<String>();
			errors.add("User input cannot be empty!");
			errorUser.setErrors(errors);
			errorUsers.add(errorUser);
			
			return errorUsers;
		}
	}
	
	private List<BlacklistUser> processExistingUser(BlacklistUser user) {
		//Check Existing Blacklist User
		Optional<BlacklistUser> existingUserOp = blacklistUserRepository.findById(user.getId());
		if(existingUserOp.isPresent()) {
			BlacklistUser existingUser = existingUserOp.get();
			user.setVersion(existingUser.getVersion());
			if(checkExistingBlacklistUser(existingUser, user)) {
				//Same name no need check
				blacklistUserRepository.save(user);
				return blacklistUserRepository.findAll();
			}else {
				//updated name need check
				return checkSaveUser(user);
			}
		}else {
			// User object null return error
			List<BlacklistUser> errorUsers = new ArrayList<BlacklistUser>();
			BlacklistUser errorUser = new BlacklistUser();
			List<String> errors = new ArrayList<String>();
			errors.add("Unable to find User Id! Id: "+ user.getId());
			errorUser.setErrors(errors);
			errorUsers.add(errorUser);
			
			return errorUsers;
		}
	}
	
	private boolean checkExistingBlacklistUser(BlacklistUser existingUser, BlacklistUser newUser) {
		if(existingUser.getFirstName().equalsIgnoreCase(newUser.getFirstName()) && 
				existingUser.getLastName().equalsIgnoreCase(newUser.getLastName()) ) {
			return true;
		}
		
		return false;
	}
	
	private List<BlacklistUser> checkSaveUser(BlacklistUser user){
		// Business validation
		List<String> errorList = validateName(user);
		if(CollectionUtils.isEmpty(errorList)) {
			//No Error
			blacklistUserRepository.save(user);
			return blacklistUserRepository.findAll();
		}else {
			//Set Error
			List<BlacklistUser> errorUsers = new ArrayList<BlacklistUser>();
			user.setErrors(errorList);
			errorUsers.add(user);
			return errorUsers;
		}
	}
	
	private List<String> validateName(BlacklistUser user){
		List<String> errors = new ArrayList<String>();
		List<BlacklistUser> users = blacklistUserRepository.getUsersByName(user.getFirstName(), user.getLastName());
		if(!CollectionUtils.isEmpty(users)) {
			errors.add("This user already blacklisted!");
		}
		
		return errors;
	}

	@Override
	public BlacklistUser getBlacklistUserById(Long id) {
		Optional<BlacklistUser> optionalUser = blacklistUserRepository.findById(id);
		if(!optionalUser.isEmpty()) {
			return optionalUser.get();
		}
		
		return null;
	}

	@Override
	@Transactional
	public void deleteBlacklistUserById(Long id) {
		blacklistUserRepository.deleteById(id);
	}

	@Override
	public Boolean checkBlacklistUser(BlacklistUser blacklistUser) {
		List<BlacklistUser> users = blacklistUserRepository.getActiveUsersByName(blacklistUser.getFirstName(), blacklistUser.getLastName());
		if(CollectionUtils.isEmpty(users)) {
			return false;
		}
		
		return true;
	}

}
