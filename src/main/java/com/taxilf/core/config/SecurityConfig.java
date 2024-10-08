package com.taxilf.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.taxilf.core.filter.JwtFilter;
import com.taxilf.core.model.enums.Role;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    SecurityConfig(JwtFilter jwtFilter){
        this.jwtFilter = jwtFilter;
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        http
        
            .authorizeHttpRequests(request -> request
                .requestMatchers("/auth/**", "/admin/**", "/trip/fare").permitAll()
                .requestMatchers("/passenger/**", "/trip/passenger/**").hasAuthority(Role.PASSENGER.name())
                .requestMatchers("/driver/**", "/trip/driver/**").hasAuthority(Role.DRIVER.name())
                .anyRequest().authenticated() 
            )
        
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .formLogin(form -> form.disable())
            .httpBasic(customizer -> customizer.disable())
            
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
            
        return http.build();
    }
}
