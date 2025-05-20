package com.DPhong.storeMe.config;

import com.DPhong.storeMe.constant.AppConstant;
import com.DPhong.storeMe.constant.RoleConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@Configuration
public class SecurityConfiguration {

    private final String BASE_URL = AppConstant.BASE_URL;
    private final String[] whiteList = {
            BASE_URL + "/auth/login",
            BASE_URL + "/auth/register",
            BASE_URL + "/auth/refresh",
            BASE_URL + "/auth/verify-email",
            BASE_URL + "/auth/forgot-password",
            BASE_URL + "/auth/reset-password",
            BASE_URL + "/oauth2/**",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    };

    private final String[] whiteListAdmin = {
            BASE_URL + "/storage-plans/**", BASE_URL + "/roles/**",
    };

    private final OAuth2AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http, JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers(whiteList)
                                        .permitAll()
                                        .requestMatchers(whiteListAdmin)
                                        .hasRole(RoleConstant.ROLE_ADMIN)
                                        .anyRequest()
                                        .authenticated())
                .oauth2ResourceServer(
                        oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)))
                .formLogin(AbstractHttpConfigurer::disable)
                .oauth2Login(
                        oauth2 ->
                                oauth2
                                        .authorizationEndpoint(
                                                endpoint -> endpoint.baseUri(BASE_URL + "/oauth2/authorize"))
                                        .redirectionEndpoint(
                                                endpoint ->
                                                        endpoint.baseUri(BASE_URL + "/oauth2/callback/{registrationId}"))
                                        .successHandler(oauth2AuthenticationSuccessHandler))
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
}
