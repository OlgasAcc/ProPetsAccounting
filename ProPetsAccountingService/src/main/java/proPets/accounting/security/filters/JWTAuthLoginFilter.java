package proPets.accounting.security.filters;

import java.io.IOException;
import java.security.Principal;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import proPets.accounting.configuration.JWTConfiguration;
import proPets.accounting.configuration.UserCredentials;
import proPets.accounting.dao.UserAccountRepository;
import proPets.accounting.model.UserAccount;
import proPets.accounting.service.JwtService;

@Component
@Order(20)

public class JWTAuthLoginFilter implements Filter {

	@Autowired
	UserAccountRepository accountRepository;

	@Autowired
	JWTConfiguration jwtConfiguration;

	@Autowired
	JwtService jwtService;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		String method = request.getMethod();
		String path = request.getServletPath();
		String auth = request.getHeader("Authorization");
		
		if (checkPointCut(path, method)) {
			UserCredentials userCredentials = null;
			try {
				userCredentials = jwtConfiguration.tokenDecode(auth);
				
			} catch (Exception e) {
				response.sendError(401, "Header Authorization not valid");
				return;
			}
			
			UserAccount userAccount = accountRepository.findById(userCredentials.getLogin()).get();
			tokenValidation (userAccount, userCredentials, response);
			
			String roles = convertSetOfRolesToString(userAccount.getRoles());
			String newToken;
			try {
				newToken = jwtService.createAuthenticationToken(userAccount.getEmail(), roles);
				response.setHeader("X-token", newToken);
				System.out.println("added x-token");

			} catch (Exception e) {
				response.sendError(404, "Token creation failed");
			}
			chain.doFilter(new WrapperRequest(request, userCredentials.getLogin()), response);
			return;
		}

		System.out.println("login filter did not work");
		chain.doFilter(request, response);
	}

	
	
	private boolean checkPointCut(String path, String method) {
		boolean check = "/account/v1/sign_in".equalsIgnoreCase(path) && "Post".equalsIgnoreCase(method);
		return check;
	}
	
	private void tokenValidation (UserAccount userAccount, UserCredentials userCredentials, HttpServletResponse response) throws IOException {
		if (userAccount == null) {
			response.sendError(401, "User not found");
			return;
		}
		if (!BCrypt.checkpw(userCredentials.getPassword(), userAccount.getPassword())) {
			response.sendError(403, "Password not valid");
			return;
		}
	}

	private String convertSetOfRolesToString(Set<String> roles) {
		String[] userRoles = roles.stream().map((r) -> r.toUpperCase()).toArray(String[]::new);
		String res = String.join(",", userRoles);
		return res;
	}

	private class WrapperRequest extends HttpServletRequestWrapper {
		String email;

		public WrapperRequest(HttpServletRequest request, String email) {
			super(request);
			this.email = email;
		}

		@Override
		public Principal getUserPrincipal() {
			return new Principal() {

				@Override
				public String getName() {
					return email;
				}
			};
		}
	}
}
