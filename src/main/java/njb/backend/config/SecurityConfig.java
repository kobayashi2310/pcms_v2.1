package njb.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.util.Collection;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/js/**").permitAll()
                        .requestMatchers("/pcms/login").anonymous()
                        .requestMatchers("/", "/pcms", "/pcms/reservation").permitAll()
                        .requestMatchers("/pcms/reservations/my-reservations", "/pcms/reservations/report-return/**").authenticated()
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/pcms/login")
                        .usernameParameter("studentId")
                        .passwordParameter("password")
                        .loginProcessingUrl("/pcms/login")
                        .defaultSuccessUrl("/pcms", true)
                        .failureUrl("/pcms/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/pcms/logout")
                        .logoutSuccessUrl("/pcms")
                );
        return http.build();
    }

    @Bean
    AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (req, res, auth) -> {
          Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
          String redirectUrl;

          if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
              redirectUrl = "/pcms/admin";
          } else {
              redirectUrl = "/pcms/";
          }

          res.sendRedirect(redirectUrl);
        };
    }

}
