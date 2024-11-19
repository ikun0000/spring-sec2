package com.example.springsec2.config;

import com.nimbusds.jose.util.StandardCharset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final OpaqueTokenIntrospector opaqueTokenIntrospector;

    public SecurityConfig(OpaqueTokenIntrospector opaqueTokenIntrospector) {
        this.opaqueTokenIntrospector = opaqueTokenIntrospector;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/resource/**");
        http.authorizeHttpRequests(req -> {
            req.requestMatchers("/resource/login").permitAll();
            req.requestMatchers("/resource/admin/**").hasRole("ADMIN");
            req.requestMatchers("/resource/user/**").hasAnyRole("USER", "ADMIN");
            req.requestMatchers("/resource/**").authenticated();
        }).oauth2ResourceServer(oauth2 -> {
            oauth2.opaqueToken(opaqueTokenConfigurer -> {
                opaqueTokenConfigurer.introspector(opaqueTokenIntrospector);
            });

            // 未认证
            oauth2.authenticationEntryPoint((request, response, authException) -> {
                // oauth2 认证失败导致的，还有一种可能是非oauth2认证失败导致的，比如没有传递token，但是访问受权限保护的方法
                if (authException instanceof OAuth2AuthenticationException oAuth2AuthenticationException) {
                    OAuth2Error error = oAuth2AuthenticationException.getError();
                    logger.warn("Authentication fail, Exception type: [{}],异常:[{}]",
                            authException.getClass().getName(), error);
                }

                response.setCharacterEncoding(StandardCharset.UTF_8.name());
                response.setContentType(MediaType.APPLICATION_JSON.toString());
                response.getWriter().write("""
                        {
                            "code": -1,
                            "msg": "该接口需要认证授权"
                        }
                        """);
            });

            // 未授权
            oauth2.accessDeniedHandler((request, response, accessDeniedException) -> {
                response.setCharacterEncoding(StandardCharset.UTF_8.name());
                response.setContentType(MediaType.APPLICATION_JSON.toString());
                response.getWriter().write("""
                        {
                            "code": -2,
                            "msg": "您没有该接口权限"
                        }
                        """);
            });
        }).csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain2(HttpSecurity http) throws Exception {
        http.securityMatcher("/admin/**");
        http.authorizeHttpRequests(req -> {
            req.requestMatchers("/admin/**").authenticated();
        }).httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

}
