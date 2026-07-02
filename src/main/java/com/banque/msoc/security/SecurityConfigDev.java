package com.banque.msoc.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.Collection;

@Configuration
@EnableWebSecurity
public class SecurityConfigDev {

    @Bean
    @Profile("dev")
    SecurityFilterChain securedDevSecurity(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/oc/dev/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/api/oc/notifications/stream").permitAll()
                        .requestMatchers("/api/oc/notifications/**").permitAll()
                        .requestMatchers("/api/oc/flows/*/decision").permitAll()
                        .requestMatchers("/api/oc/flows/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(this::convertJwt))
                )
                .build();
    }

    private AbstractAuthenticationToken convertJwt(Jwt jwt) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        Object rolesClaim = jwt.getClaim("roles");

        if (rolesClaim instanceof Collection<?> roles) {
            for (Object role : roles) {
                String roleName = String.valueOf(role).trim();
                if (!roleName.isBlank()) {
                    authorities.add(new SimpleGrantedAuthority(roleName));
                }
            }
        }

        return new JwtAuthenticationToken(jwt, authorities, jwt.getSubject());
    }
}