package com.kevindai.oauth2server.config;

import com.kevindai.oauth2server.filter.TokenAuthenticationFilter;
import com.kevindai.oauth2server.service.oauth2.CustomOAuth2UserService;
import com.kevindai.oauth2server.service.oauth2.CustomOidcUserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserDetailsService userDetailsService;
    private final TokenAuthenticationFilter tokenAuthenticationFilter;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOidcUserService customOidcUserService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/actuator/**").permitAll()
                                .requestMatchers("/static/**").permitAll()
                                .requestMatchers("/").permitAll().requestMatchers(new RegexRequestMatcher(".*\\.js", null)).permitAll()
                                .requestMatchers(new RegexRequestMatcher(".*\\.css", null)).permitAll()
                                .requestMatchers("/index.html").permitAll()
                                .requestMatchers("/swagger-resources/**").permitAll()
                                .requestMatchers("/test/**").permitAll()
                                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                                .requestMatchers("/login", "/login/**").permitAll()
                                .anyRequest().authenticated()
                )
                .with(new OAuth2LoginConfigurer<>(), oauth2 ->
                        oauth2
                                .userInfoEndpoint(userInfo ->
                                        userInfo
                                                .userService(customOAuth2UserService)
                                                .oidcUserService(customOidcUserService))
                                .authorizationEndpoint(authorization -> authorization.baseUri("/oauth2/authorization"))
                                .redirectionEndpoint(redirection -> redirection.baseUri("/login/oauth2/code/*"))
                                .defaultSuccessUrl("/", true) // we can set oauth2 related configs here
                                .successHandler(customOAuth2SuccessHandler)
                                .failureHandler((request, response, exception) -> {
                                    exception.printStackTrace();
                                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "OAuth2 Login Failed: " + exception.getMessage());
                                })
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/index.html")
                        .invalidateHttpSession(true)
                )
                .exceptionHandling(ex -> ex
                                //For /user and API endpoints: return 401 instead of redirect
                                .defaultAuthenticationEntryPointFor(
                                        (request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED),
                                        new AntPathRequestMatcher("/user")
                                )
                                //Fallback: UI flow still redirects to login page
//                        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/"))
                                .authenticationEntryPoint(
                                        (request, response, authException) -> {
                                            // This is hit when token exchange or login fails
                                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                            response.getWriter().write("Login failed: " + authException.getMessage());
                                        }
                                )
                )
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
        ;
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    private PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
//
//    public static void main(String[] args) {
//        DelegatingPasswordEncoder delegatingPasswordEncoder = (DelegatingPasswordEncoder) PasswordEncoderFactories.createDelegatingPasswordEncoder();
//        delegatingPasswordEncoder.setDefaultPasswordEncoderForMatches(NoOpPasswordEncoder.getInstance());
//        System.out.println(delegatingPasswordEncoder.encode("kevindai"));
//    }
}
