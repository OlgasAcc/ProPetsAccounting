package proPets.accounting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import proPets.accounting.dao.UserAccountRepository;
import proPets.accounting.service.security.JWTUtil;

@Service
public class JwtServiceImpl implements JwtService {

	@Autowired
	UserAccountService userAccountService;

	@Autowired
	UserAccountRepository accountRepository;

	@Autowired
	JWTUtil jwtTokenUtil;

	@Override
	public String createAuthenticationToken(String email, String roles) throws Exception {
		String newToken = jwtTokenUtil.generateToken(email, roles);
		return newToken;
	}
	
	@Override
	public String getEmailFromToken(String token) {
		String email = jwtTokenUtil.getEmailFromToken(token);
		return email;
	}

	@Override
	public String tokenValidation(String tokenToAuth) throws Exception {
		String newToken = null;
		if (jwtTokenUtil.validateToken(tokenToAuth)) {
			String email = jwtTokenUtil.getEmailFromToken(tokenToAuth);
			String roles = jwtTokenUtil.getRolesFromToken(tokenToAuth);
			newToken = jwtTokenUtil.generateToken(email, roles);
		}
		return newToken;
	}

}
