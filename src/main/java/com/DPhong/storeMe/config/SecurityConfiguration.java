package com.DPhong.storeMe.config;

import com.DPhong.storeMe.constant.AppConstant;
import com.DPhong.storeMe.enums.RoleName;
import com.DPhong.storeMe.interceptor.PermissionInterceptor;
import com.DPhong.storeMe.security.jwt.CustomJwtAuthenticationConverter;
import com.DPhong.storeMe.security.oauth2.CustomOAuth2UserService;
import com.DPhong.storeMe.security.filter.EnsureUserExistsFilter;
import com.DPhong.storeMe.security.filter.JwtBlacklistFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfiguration implements WebMvcConfigurer {

  private final PermissionInterceptor permissionInterceptor;

  private final String BASE_URL = AppConstant.BASE_URL;
  private final String[] whiteList = {
    BASE_URL + "/auth/login",
    BASE_URL + "/auth/register",
    BASE_URL + "/auth/refresh",
    BASE_URL + "/auth/verify-email",
    BASE_URL + "/auth/forgot-password",
    BASE_URL + "/auth/reset-password",
    BASE_URL + "/auth/registration/send-email",
    BASE_URL + "/oauth2/**",
    BASE_URL + "/2fa/verify-totp",
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
      HttpSecurity http,
      CustomAuthenticationEntryPoint authenticationEntryPoint,
      CustomJwtAuthenticationConverter jwtAuthenticationConverter,
      CustomOAuth2UserService customOAuth2UserService,
      EnsureUserExistsFilter ensureUserExistsFilter,
      JwtBlacklistFilter jwtBlacklistFilter)
      throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(whiteList)
                    .permitAll()
                    .requestMatchers(whiteListAdmin)
                    .hasAnyRole(RoleName.SUPER_ADMIN.getName(), RoleName.ADMIN.getName())
                    .anyRequest()
                    .authenticated())
        .oauth2ResourceServer(
            oauth2 ->
                oauth2
                    .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter))
                    .authenticationEntryPoint(authenticationEntryPoint))
        .addFilterBefore(jwtBlacklistFilter, BearerTokenAuthenticationFilter.class)
        .addFilterAfter(ensureUserExistsFilter, BearerTokenAuthenticationFilter.class)
        .formLogin(AbstractHttpConfigurer::disable)
        .oauth2Login(
            oauth2 ->
                oauth2
                    .authorizationEndpoint(
                        endpoint -> endpoint.baseUri(BASE_URL + "/oauth2/authorize"))
                    .redirectionEndpoint(
                        endpoint ->
                            endpoint.baseUri(BASE_URL + "/oauth2/callback/{registrationId}"))
                    .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                    .successHandler(oauth2AuthenticationSuccessHandler))
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint));
    return http.build();
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(permissionInterceptor).excludePathPatterns(whiteList);
  }
}
