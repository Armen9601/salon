package com.gevorgyan.salon.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  public InMemoryUserDetailsManager userDetailsService(
      @Value("${app.admin.username}") String username,
      @Value("${app.admin.password}") String password) {

    // {noop} for dev simplicity. Replace with BCrypt in production.
    UserDetails admin = User.withUsername(username)
        .password("{noop}" + password)
        .roles("ADMIN")
        .build();

    return new InMemoryUserDetailsManager(admin);
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/assets/**",
                "/",
                "/barbers/**",
                "/book",
                "/book/**",
                "/h2-console/**",
                "/admin/login"
            ).permitAll()
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .anyRequest().permitAll())
        .formLogin(form -> form
            .loginPage("/admin/login")
            .defaultSuccessUrl("/admin", true)
            .permitAll())
        .logout(logout -> logout
            .logoutUrl("/admin/logout")
            .logoutSuccessUrl("/")
            .permitAll())
        // H2 console
        .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
        .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
        .build();
  }
}

