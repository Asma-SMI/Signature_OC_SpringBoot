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
@Profile("prod")
public class SecurityConfigProd {
    @Bean
    SecurityFilterChain prodSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error").permitAll()

                        // en prod, éviter d'exposer les endpoints dev
                       // .requestMatchers("/api/oc/dev/**").denyAll()

                        // consultation des flux
                        .requestMatchers("/api/oc/flows/**")
                        .hasAnyAuthority("OC_CONSULTATION", "OC_DECISION", "OC_ADMIN")

                        // décision métier
                        .requestMatchers("/api/oc/flows/*/decision")
                        .hasAnyAuthority("OC_DECISION", "OC_ADMIN")

                        // notifications
                        .requestMatchers("/api/oc/notifications/stream").permitAll()
                        .requestMatchers("/api/oc/notifications/**")
                        .hasAnyAuthority("OC_CONSULTATION", "OC_DECISION", "OC_ADMIN")

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
