package proPets.accounting.service.utils;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import proPets.accounting.configuration.JWTConfiguration;
import proPets.accounting.dao.UserAccountRepository;

@Component

public class JWTUtil implements Serializable {

	@Autowired
	UserAccountRepository accountRepository;

	@Autowired
	JWTConfiguration jwtConfiguration;

	private static final long serialVersionUID = -2550185165626007488L;

	public String getEmailFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	public String getRolesFromToken(String token) {
		Claims jwsMap = Jwts.parser().setSigningKey(jwtConfiguration.getSecret()).parseClaimsJws(token).getBody();
		return (String) jwsMap.get("roles");
	}

	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(jwtConfiguration.getSecret()).parseClaimsJws(token).getBody();
	}

	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	public String generateToken(String email, String roles) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("roles", roles);
		return doGenerateToken(claims, email);
	}

	private String doGenerateToken(Map<String, Object> claims, String subject) {
		return Jwts.builder().setClaims(claims).setSubject(subject)
				.setExpiration(
						new Date(System.currentTimeMillis() + jwtConfiguration.getExpPeriodValue() * 60 * 60 * 1000))
				.signWith(SignatureAlgorithm.HS512, jwtConfiguration.getSecret()).compact();
	}

//validate token
	public Boolean validateToken(String token) {

		final String email = getEmailFromToken(token);
		return (accountRepository.existsById(email) && !isTokenExpired(token));
	}
}
