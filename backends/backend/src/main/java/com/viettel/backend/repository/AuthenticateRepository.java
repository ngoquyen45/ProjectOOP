package com.viettel.backend.repository;

import com.viettel.backend.domain.User;

public interface AuthenticateRepository {
	
	public User findByUsername(String username);
	
}
