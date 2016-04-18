package pr.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
//	public static void main(String[] args) {
//		BCryptPasswordEncoder pe = new BCryptPasswordEncoder(12);
//		System.out.println(pe.encode("qwe"));
//	}
	@Autowired
	@Qualifier("UserDetailsServiceImpl")
	UserDetailsService userDetailsService;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.httpBasic().and().authorizeRequests()
				.antMatchers("/", "/login", "/index.html", "/html/**/*.html", "/**/main.html",
						"/fonts/**", "/resources/**", "/lang/**")
				.permitAll().anyRequest()
				.authenticated()
				.and().csrf()
				.csrfTokenRepository(csrfTokenRepository())
				.and()
				.addFilterAfter(csrfHeaderFilter(), CsrfFilter.class)
				;
	}
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder(12));
	}

	private Filter csrfHeaderFilter() {
		final String ORIGIN = "Origin";
		return new OncePerRequestFilter() {
			@Override
			protected void doFilterInternal(HttpServletRequest request,
					HttpServletResponse response, FilterChain filterChain)
					throws ServletException, IOException {
				CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
				if (csrf != null) {
					Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
					String token = csrf.getToken();

					if (cookie == null || token != null
							&& !token.equals(cookie.getValue())) {
						
						cookie = new Cookie("XSRF-TOKEN", token);
						cookie.setPath("/");
						response.addCookie(cookie);

						String origin = request.getHeader(ORIGIN);
						if(origin != null && origin.toLowerCase().startsWith("http://localhost:808")){
//							System.out.println("1 - " + request.getMethod());
							response.setHeader("Access-Control-Allow-Origin", "*");
							response.setHeader("Access-Control-Allow-Credentials", "true");
							response.setHeader("Access-Control-Allow-Headers",
									request.getHeader("Access-Control-Request-Headers"));
						}
					}
					filterChain.doFilter(request, response);
				}
			}
		};
	}

	private CsrfTokenRepository csrfTokenRepository() {
		HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
		repository.setHeaderName("X-XSRF-TOKEN");
		return repository;
	}
}