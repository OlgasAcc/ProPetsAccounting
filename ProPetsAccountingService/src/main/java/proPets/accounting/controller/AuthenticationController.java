package proPets.accounting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import proPets.accounting.dto.AuthResponse;
import proPets.accounting.exceptions.ForbiddenException;
import proPets.accounting.service.JwtServiceImpl;

@RestController
@RequestMapping("/security/v1")

public class AuthenticationController {

	@Autowired
	JwtServiceImpl jwtUserDetailsServiceImpl;

// for all microservices besides accounting!

	@RequestMapping(value = "/verify", method = RequestMethod.POST)
	public ResponseEntity<AuthResponse> tokenValidation(@RequestHeader(value = "Authorization") String token)
			throws Exception {
		String newToken = jwtUserDetailsServiceImpl.tokenValidation(token.substring(7));
		if (newToken != null) {
			HttpHeaders newHeaders = new HttpHeaders();
			newHeaders.add("X-token", newToken);
			newHeaders.add("Content-Type", "application/json");
			String userId = jwtUserDetailsServiceImpl.getEmailFromToken(newToken);
			AuthResponse authResponse = new AuthResponse();
			authResponse.setEmail(userId);
			return ResponseEntity.ok().headers(newHeaders).body(authResponse);
		} else
			throw new ForbiddenException();
	}
}



//if auth.service will be separate from accounting - оставляю на всякий случай

	//@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	//public ResponseEntity<String> createAuthenticationToken(@RequestParam String email, @RequestParam String roles)
	//		throws Exception {
	//	String newToken = jwtUserDetailsServiceImpl.createAuthenticationToken(email, roles);
	//	HttpHeaders newHeaders = new HttpHeaders();
	//	newHeaders.add("Authorization", "Bearer " + newToken);
	//	return ResponseEntity.ok().headers(newHeaders).body("");
	//}
