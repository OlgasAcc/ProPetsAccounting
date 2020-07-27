package proPets.accounting.security.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Service
@Order(Ordered.HIGHEST_PRECEDENCE)

public class CORSFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		final String origin = (request.getHeader("Origin"));

		if (origin != null && origin.equals("http://localhost:3000")) {
			response.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
			response.addHeader("Access-Control-Allow-Credentials", "true");
			response.addHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS");
			response.addHeader("Access-Control-Max-Age", "86400");
			// response.addHeader("Access-Control-Allow-Headers",
			// "Authorization, X-Token, X-id, Origin, Content-Type, Accept,
			// Access-Control-Request-Method, Access-Control-Request-Headers");
			// response.addHeader("Access-Control-Expose-Headers", "Authorization,
			// xsrf-token, X-Token, X-id");

			if (request.getHeader("Access-Control-Request-Method") != null && "OPTIONS".equals(request.getMethod())) {
				response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
				response.addHeader("Access-Control-Allow-Headers",
						"Authorization, X-Token, X-id, Origin, Content-Type, Accept, Access-Control-Request-Method, Access-Control-Request-Headers");
				response.addHeader("Access-Control-Expose-Headers", "Authorization, xsrf-token, X-Token, X-id");
				response.setStatus(200);
			}

			chain.doFilter(request, res);
		}
	}
}