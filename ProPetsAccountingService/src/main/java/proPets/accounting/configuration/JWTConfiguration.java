package proPets.accounting.configuration;

import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import proPets.accounting.exceptions.UserAuthenticationException;
import proPets.accounting.model.UserAccount;

@Configuration
@RefreshScope
public class JWTConfiguration {

	Map<String, UserAccount> authenticatedUsers = new ConcurrentHashMap<>();

	//test
	@Value("${jwt.message}")
	String message;
	
	//test
	@RefreshScope
	public String getMessage() {
		return message;
	}

	@Value("${jwt.secret}")
	String secret;

	@RefreshScope
	public String getSecret() {
		return secret;
	}

	@Value("${jwt.expPeriodValue}")
	long expPeriodValue;

	@RefreshScope
	public long getExpPeriodValue() {
		return expPeriodValue;
	}

	public UserCredentials tokenDecode(String token) {
		try {
			int pos = token.indexOf(" ");
			token = token.substring(pos + 1);
			String credential = new String(Base64.getDecoder().decode(token));
			String[] credentials = credential.split(":");
			return new UserCredentials(credentials[0], credentials[1]);
		} catch (Exception e) {
			throw new UserAuthenticationException();
		}
	}
}
