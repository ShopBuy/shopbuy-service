package com.codegym.shopbuyservice.config;

import com.codegym.shopbuyservice.security.JwtAuthEntryPoint;
import com.codegym.shopbuyservice.security.JwtAuthFilter;
import com.codegym.shopbuyservice.service.SecurityService;
import com.codegym.shopbuyservice.service.impl.SecurityServiceImpl;
import com.codegym.shopbuyservice.service.impl.UserDetailsServiceImpl;
import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableAutoConfiguration
@EnableAsync
@EnableWebSecurity
public class SecurityConfiguration {
   @Autowired
   private UserDetailsServiceImpl userDetailsService;

   @Autowired
   private JwtAuthEntryPoint authEntryPoint;

   @Bean
   public SecurityService securityService() {
      return new SecurityServiceImpl();
   }

   @Bean
   public PasswordEncoder bCryptPasswordEncoder() {
      return new BCryptPasswordEncoder();
   }

   @Bean
   public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
      return http.getSharedObject(AuthenticationManagerBuilder.class).build();
   }

   @Bean
   public Filter jwtAuthenticationFilter() {
      return new JwtAuthFilter();
   }

   @Autowired
   public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
      auth.userDetailsService(userDetailsService)
              .passwordEncoder(new BCryptPasswordEncoder());
   }

   @Bean
   public CorsConfigurationSource corsConfigurationSource() {
      CorsConfiguration corsConfig = new CorsConfiguration();
      corsConfig.addAllowedOrigin("*");
      corsConfig.addAllowedHeader("*");
      corsConfig.addAllowedMethod("*");
      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      source.registerCorsConfiguration("/**", corsConfig);
      return source;
   }

   @Bean
   protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
      http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
              .csrf(AbstractHttpConfigurer::disable);

      http.exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(authEntryPoint)
                      .accessDeniedPage("/api/auth/access-denied"))
              .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
      http.authorizeHttpRequests((authorize) -> authorize
              .requestMatchers("/api/auth/**").permitAll());
      http.authorizeHttpRequests((authorize) -> authorize
              .requestMatchers("/api/home/**").permitAll());
      http.authorizeHttpRequests((authorize) -> authorize
              .requestMatchers("/api/admin/**").permitAll());
      http.authorizeHttpRequests((authorize) -> authorize
              .requestMatchers("/api/users/**").permitAll());
      http.authorizeHttpRequests((authorize) -> authorize
              .requestMatchers("/api/categories/**").permitAll());
      http.authorizeHttpRequests((authorize) -> authorize
              .requestMatchers("/api/subCategories/**").permitAll());
      http.authorizeHttpRequests((authorize) -> authorize
              .requestMatchers("/api/products/**").permitAll());

      http.rememberMe((remember) -> remember
              .tokenRepository(this.persistentTokenRepository())
              .tokenValiditySeconds(24 * 60 * 60)
      );


      // Use JwtAuthorizationFilter to check token -> get user info
      http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
      return http.build();
   }

   public PersistentTokenRepository persistentTokenRepository() {
      return new InMemoryTokenRepositoryImpl();
   }
}



