package proPets.accounting.configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import proPets.accounting.model.UserAccount;

// to remove or move the beans from jwt config
public class AccountConfiguration {
	
	Map<String, UserAccount> users = new ConcurrentHashMap<>();	
	
	public boolean addUser (String sessionId, UserAccount userAccount) {
		return users.put(sessionId,  userAccount)==null;
	}
	
	public UserAccount getUser(String sessionId) {
		return users.get(sessionId);
	}
	
	public String getUserLogin(String sessionId) {
		return users.get(sessionId).getEmail();
	}
	
	public UserAccount removeUser(String sessionId) {
		return users.remove(sessionId);
	}
}
