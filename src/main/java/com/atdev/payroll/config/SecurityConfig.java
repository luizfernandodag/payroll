package com.atdev.payroll.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Value("${payroll.user}")
    private String username;

    @Value("${payroll.password}")
    private String password;

    // Configuração do usuário em memória
    @Bean
    public InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configuração de segurança
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
      http
        .csrf(csrf -> csrf.disable()) // API REST não precisa de CSRF
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/payroll/**").hasRole("ADMIN") // apenas payroll exige autenticação
            .anyRequest().permitAll() // outras rotas liberadas
        )
        .httpBasic(); // habilita Basic Auth

    // Configuração de sessão stateless e desativa request cache
    http
        .sessionManagement(session -> 
            session.sessionCreationPolicy(
                org.springframework.security.config.http.SessionCreationPolicy.STATELESS
            )
        )
        .requestCache(cache -> cache.disable());

    return http.build();
    }
}
