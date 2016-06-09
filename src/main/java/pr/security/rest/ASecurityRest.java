package pr.security.rest;

import javax.annotation.Resource;

import org.springframework.security.core.context.SecurityContextHolder;

import pr.security.db.SecureDatabaseAPI;
import pr.security.model.IUser;

public abstract class ASecurityRest {
	
	@Resource(name="SecureDatabaseAPI")
	public SecureDatabaseAPI dao;
	@Resource(type = Common4rest.class)
	public Common4rest comm;
	
	public IUser getCurrentUser() {
		String userName = SecurityContextHolder.getContext().getAuthentication().getName();
		return (IUser) dao.getUserByLogin(null, userName);
	}
}