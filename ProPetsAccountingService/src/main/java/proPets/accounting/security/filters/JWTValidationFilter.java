package proPets.accounting.security.filters;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import proPets.accounting.dao.UserAccountRepository;
import proPets.accounting.service.JwtService;

@Component
@Order(30)

public class JWTValidationFilter implements Filter {

	@Autowired
	UserAccountRepository accountRepository;

	@Autowired
	JwtService jwtService;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		String path = request.getServletPath();
		String auth = request.getHeader("Authorization");

		if (path.startsWith("/account/v1/user")) {
			if (auth.startsWith("Bearer")) {
				String newToken;
				try {
					newToken = jwtService.tokenValidation(auth.substring(7));
				} catch (Exception e) {
					response.sendError(401, "Header Authorization is not valid");
					return;
				}
				response.setHeader("X-token", newToken);
				String email = jwtService.getEmailFromToken(newToken);
				chain.doFilter(new WrapperRequest(request, email), response);
				return;
			} else {
				response.sendError(401, "Token format is wrong");
				return;
			}
		}
		System.out.println("validation filter did not work");
		chain.doFilter(request, response);
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
// later - make it depends on the role admin
//	private boolean checkPointCut(String path, String method) {
//		boolean check = "/actuator/refresh".equalsIgnoreCase(path) && "Post".equalsIgnoreCase(method);
//		return check;
//	}
}
