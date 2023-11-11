package com.example.bankApp.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityFilterChainConfig{
private final AuthenticationProvider authenticationProvider;
private final JwtFilterConfiguration filterConfiguration;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers("/account/**", "/api/users/**", "/api/users/logout", "/api/messages/**").permitAll())
//                .authorizeHttpRequests(authorize ->
//                        authorize
//                                .requestMatchers("/swagger-ui.html").authenticated() // Require authentication for Swagger UI
//                                .requestMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources", "/configuration/security", "/swagger-ui.html", "/webjars/**").permitAll() // Allow public access to Swagger resources
//                                .anyRequest().authenticated())
//                .authorizeHttpRequests(authorize ->
//                        authorize
//                                .requestMatchers("/api/users/*"))

                .sessionManagement((session) ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(filterConfiguration, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
