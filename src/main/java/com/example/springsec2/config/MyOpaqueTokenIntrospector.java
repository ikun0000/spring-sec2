package com.example.springsec2.config;

import com.example.springsec2.service.TokenService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@Component
public class MyOpaqueTokenIntrospector implements OpaqueTokenIntrospector {
    private final TokenService tokenService;

    public MyOpaqueTokenIntrospector(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public OAuth2AuthenticatedPrincipal introspect(String token) {
        UserDetails userDetails = tokenService.verifyToken(token);

        return new OAuth2AuthenticatedPrincipal() {
            @Override
            public Map<String, Object> getAttributes() {
                Map<String, Object> attribute = Map.of("active", userDetails.isEnabled());
                return attribute;
            }

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return userDetails.getAuthorities();
            }

            @Override
            public String getName() {
                return userDetails.getUsername();
            }
        };
    }
}
