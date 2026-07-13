package com.banque.msoc.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProvider {
    public String getCurrentUserEmail() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return "Utilisateur";
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof Jwt jwt) {
            String subject = jwt.getSubject();

            if (subject != null && !subject.isBlank()) {
                return subject;
            }

            Object emailClaim = jwt.getClaims().get("email");

            if (emailClaim != null && !String.valueOf(emailClaim).isBlank()) {
                return String.valueOf(emailClaim);
            }
        }

        if (authentication.getName() != null && !authentication.getName().isBlank()) {
            return authentication.getName();
        }

        return "Utilisateur";
    }
}
