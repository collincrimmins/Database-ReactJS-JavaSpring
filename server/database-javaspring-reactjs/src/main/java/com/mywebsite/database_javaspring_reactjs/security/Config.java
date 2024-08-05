package com.mywebsite.database_javaspring_reactjs.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class Config {
    @Autowired
    private JwtAuthFilter authFilter;

    // User Creation
    @Bean
    public UserDetailsService userDetailsService() {
        return new JWTUserService();
    }

    // CORS Configuration
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // Requests
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(
            Arrays.asList("http://localhost:5173")
        );
        configuration.setAllowedMethods(
            Arrays.asList("GET", "POST",  "PUT", "PATCH", "DELETE", "OPTIONS")
        );
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(
            List.of("*")
        );

        // Destinations
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    // Configuring HttpSecurity
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http 

                .exceptionHandling((c) -> c.authenticationEntryPoint(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
                ))

                .sessionManagement(session -> 
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .cors((cors) -> cors
                    .configurationSource(corsConfigurationSource())
                )

                //.csrf(csrf -> csrf.disable())
                .csrf((csrf) -> csrf
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())   
                    .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())            
                )
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)

                .authorizeHttpRequests((request) ->
                    request.anyRequest().permitAll()
                )
                // .authorizeHttpRequests(
                //     auth -> auth.requestMatchers(
                //         "/csfr",
                //         "/auth/checkToken",
                //         "/**"
                //     ).permitAll()
                // )
                // .authorizeHttpRequests(
                //     auth -> auth.requestMatchers(
                //         "/auth/user/**",
                //         "/auth/admin/**"
                //     ).authenticated()
                // )

                .authenticationProvider(authenticationProvider())
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    // Password Encoding
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

final class SpaCsrfTokenRequestHandler extends CsrfTokenRequestAttributeHandler {
	private final CsrfTokenRequestHandler delegate = new XorCsrfTokenRequestAttributeHandler();

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, Supplier<CsrfToken> csrfToken) {
		/*
		 * Always use XorCsrfTokenRequestAttributeHandler to provide BREACH protection of
		 * the CsrfToken when it is rendered in the response body.
		 */
		this.delegate.handle(request, response, csrfToken);
	}

	@Override
	public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
		/*
		 * If the request contains a request header, use CsrfTokenRequestAttributeHandler
		 * to resolve the CsrfToken. This applies when a single-page application includes
		 * the header value automatically, which was obtained via a cookie containing the
		 * raw CsrfToken.
		 */
		if (StringUtils.hasText(request.getHeader(csrfToken.getHeaderName()))) {
			return super.resolveCsrfTokenValue(request, csrfToken);
		}
		/*
		 * In all other cases (e.g. if the request contains a request parameter), use
		 * XorCsrfTokenRequestAttributeHandler to resolve the CsrfToken. This applies
		 * when a server-side rendered form includes the _csrf request parameter as a
		 * hidden input.
		 */
		return this.delegate.resolveCsrfTokenValue(request, csrfToken);
	}
}

final class CsrfCookieFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(
        HttpServletRequest request, 
        HttpServletResponse response, 
        FilterChain filterChain)
			throws ServletException, IOException {
		CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
		// Render the token value to a cookie by causing the deferred token to be loaded
		csrfToken.getToken();

		filterChain.doFilter(request, response);
	}
}