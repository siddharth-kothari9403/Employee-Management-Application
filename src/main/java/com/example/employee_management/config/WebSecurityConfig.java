package com.example.employee_management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final UserDetailsService jwtUserDetailsService;
    private final JwtRequestFilter jwtRequestFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public WebSecurityConfig(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                             UserDetailsService jwtUserDetailsService,
                             JwtRequestFilter jwtRequestFilter) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(jwtUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean(name = "securityFilterChain")
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .anonymous(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((configurer) -> {
                    configurer.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                            .requestMatchers(HttpMethod.POST, "/v1/authenticate").permitAll()
                            .requestMatchers(HttpMethod.POST, "/v1/register_user", "/v1/register_hr_manager", "/v1/register_admin").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.GET, "/v1/employee/", "/v1/employee/departments").hasAnyRole("ADMIN", "HR_MANAGER")
                            .requestMatchers(HttpMethod.POST, "/v1/employee/add/**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.PUT, "/v1/employee/update").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.DELETE, "/v1/employee/delete/**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.GET, "/v1/employee/**").hasAnyRole("ADMIN", "HR_MANAGER", "USER")
                            .requestMatchers(HttpMethod.GET, "/v1/times/", "/v1/times/departments").hasAnyRole("ADMIN", "HR_MANAGER")
                            .requestMatchers(HttpMethod.POST, "/v1/times/login/**", "/v1/times/logout/**").hasAnyRole("ADMIN", "HR_MANAGER", "USER")
                            .requestMatchers(HttpMethod.GET, "/v1/times/**", "/v1/times/employee/**").hasAnyRole("ADMIN", "HR_MANAGER", "USER")
                            .anyRequest().authenticated();
                })
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}