package proPets.accounting.service;

public interface JwtService {
	
	String createAuthenticationToken(String email, String roles) //if this service will be separate
			throws Exception;
	
	String getEmailFromToken(String token);
	
	String tokenValidation(String tokenToAuth) //for outside microservices
			throws Exception;
}
