package proPets.accounting.configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import proPets.accounting.model.UserAccount;

@Configuration
public class AccountConfiguration {
	
	Map<String, UserAccount> users = new ConcurrentHashMap<>();	
	
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
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
