package com.blacklist.user.account.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.blacklist.user.account.model.BlacklistUser;

public interface BlacklistUserService {
	List<BlacklistUser> getAllBlacklistUserList();
	List<BlacklistUser> createUpdateBlacklistUser(BlacklistUser blacklistUser);
	BlacklistUser getBlacklistUserById(Long id);
	void deleteBlacklistUserById(Long id);
	Page<BlacklistUser> getAllBlacklistUserPage(int pageNo, int pageSize);
	Boolean checkBlacklistUser(BlacklistUser blacklistUser);
}
