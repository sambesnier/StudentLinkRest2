package com.sambesnier.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.sambesnier.db.dao.impl.Repository;
import com.sambesnier.db.models.AuthenticationToken;
import com.sambesnier.db.models.User;

public class Authenticator {

	public static void removeExistingToken(User user, Repository repo) {
		
		Map<String, Object> prop = new HashMap<String, Object>();
		prop.put("login", user.getUsername());
		AuthenticationToken token = (AuthenticationToken) repo.queryObject("AuthenticationToken.findByUserName", prop);
		
		repo.delete(token);
				
	}

	public static boolean isValidToken(AuthenticationToken _token, Repository repo) {
		
		Map<String, Object> prop = new HashMap<String, Object>();
		prop.put("login", _token.getUsername());
		AuthenticationToken token = (AuthenticationToken) repo.queryObject("AuthenticationToken.findByUserName", prop);
		
		if ( token.getCreated_at().getTime() >= new Date().getTime() - (3600*12*1000) ) {
			return true;
		}
		
		return false;
		
	}

}
