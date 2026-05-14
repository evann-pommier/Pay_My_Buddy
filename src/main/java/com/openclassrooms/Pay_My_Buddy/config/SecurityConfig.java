package com.openclassrooms.Pay_My_Buddy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http
	        .csrf(csrf -> csrf
	            .csrfTokenRepository(
	                CookieCsrfTokenRepository.withHttpOnlyFalse()
	            )
	        )
	        .authorizeHttpRequests(auth -> auth
	        	    .requestMatchers("/login", "/register", "/css/**", "/js/**").permitAll()
	        	    .requestMatchers("/api/auth/**").permitAll()
	        	    .anyRequest().authenticated()
	        )
	        .formLogin(form -> form
	            .loginPage("/login")
	            .usernameParameter("email")
	            .defaultSuccessUrl("/home", true)
	            .permitAll()
	        )
	        .logout(logout -> logout
	            .logoutSuccessUrl("/login?logout")
	            .permitAll()
	        )
	        .exceptionHandling(ex -> ex
	            .defaultAuthenticationEntryPointFor(
	                (request, response, authException) ->
	                    response.sendError(401, "Unauthorized"),
	                PathPatternRequestMatcher.withDefaults().matcher("/api/**")
	            )
	        );
	    return http.build();
	}

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}