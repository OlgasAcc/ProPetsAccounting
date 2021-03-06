package proPets.accounting.service.utils;

import java.net.URI;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.RequestEntity.BodyBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import proPets.accounting.configuration.AccountConfiguration;
import proPets.accounting.dto.UserRemoveDto;
import proPets.accounting.dto.UserStatesDto;
import proPets.accounting.model.UserAccount;

@Component

public class AccountUtil {
	
	@Autowired
	AccountConfiguration accConfiguration;
	
	@Async("processExecutor")
	public CompletableFuture<Boolean> removeUserDataInExternalService(UserRemoveDto userRemoveDto,String url)  {
		RestTemplate restTemplate = accConfiguration.restTemplate();
		HttpHeaders newHeaders = new HttpHeaders();
		newHeaders.add("Content-Type", "application/json");
		BodyBuilder requestBodyBuilder = RequestEntity.method(HttpMethod.DELETE, URI.create(url)).headers(newHeaders);
		RequestEntity<UserRemoveDto> request = requestBodyBuilder.body(userRemoveDto);
		ResponseEntity<String> newResponse = restTemplate.exchange(request, String.class);
		return CompletableFuture.completedFuture(newResponse.getStatusCode() == HttpStatus.OK);
	}
	
	public UserStatesDto userAccountToUserStatesDto(UserAccount userAccount) {
		return UserStatesDto.builder().email(userAccount.getEmail()).name(userAccount.getName())
				.avatar(userAccount.getAvatar()).phone(userAccount.getPhone()).fb_link(userAccount.getFb_link())
				.isBlocked(userAccount.getIsBlocked()).roles(userAccount.getRoles())
				.build();
	}

	public String convertSetOfRolesToString(Set<String> roles) {
		String[] userRoles = roles.stream().map((r) -> r.toUpperCase()).toArray(String[]::new);
		String res = String.join(",", userRoles);
		return res;
	}

}
