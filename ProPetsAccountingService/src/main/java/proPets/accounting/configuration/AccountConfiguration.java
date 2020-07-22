package proPets.accounting.configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import proPets.accounting.model.UserAccount;

@Configuration
@RefreshScope
public class AccountConfiguration {
	
	Map<String, UserAccount> users = new ConcurrentHashMap<>();	
	
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
	@Value("${default.avatar.url}")
	String defaultAvatarUrl;

	@RefreshScope
	public String getDefaultAvatarUrl() {
		return defaultAvatarUrl;
	}
	
	@Value("${messaging.cleaner.url}")
	String messagingCleanerUrl;

	@RefreshScope
	public String getMessagingCleanerUrl() {
		return messagingCleanerUrl;
	}
	
	@Value("${lostFound.cleaner.url}")
	String lostFoundCleanerUrl;

	@RefreshScope
	public String getLostFoundCleanerUrl() {
		return lostFoundCleanerUrl;
	}
	
	@Value("${searching.cleaner.url}")
	String searchingCleanerUrl;

	@RefreshScope
	public String getSearchingCleanerUrl() {
		return searchingCleanerUrl;
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
