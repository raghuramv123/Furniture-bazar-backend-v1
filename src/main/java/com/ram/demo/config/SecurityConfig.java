
package com.ram.demo.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ram.demo.security.JwtAuthFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SecurityConfig(JwtAuthFilter jwtAuthFilter,
                          @Lazy UserDetailsService userDetailsService,
                          PasswordEncoder passwordEncoder) {
        this.jwtAuthFilter      = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder    = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
            	    .requestMatchers("/api/auth/**").permitAll()
            	    .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
            	    .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
            	    .requestMatchers("/api/upload/files/**").permitAll()  // serve images publicly
            	    .requestMatchers(HttpMethod.POST, "/api/upload/**").hasRole("ADMIN")
            	    .requestMatchers(HttpMethod.POST, "/api/products/**").hasRole("ADMIN")
            	    .requestMatchers(HttpMethod.PUT,  "/api/products/**").hasRole("ADMIN")
            	    .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")
            	    .requestMatchers(HttpMethod.POST, "/api/categories/**").hasRole("ADMIN")
            	    .requestMatchers(HttpMethod.PUT,  "/api/categories/**").hasRole("ADMIN")
            	    .requestMatchers("/api/admin/**").hasRole("ADMIN")
            	    .requestMatchers("/h2-console/**").permitAll()
            	    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
            	    .anyRequest().authenticated()
            	)
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        http.headers(headers ->
            headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
//        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);  // injected, not created here
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}