package njb.pcms.config;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationSuccessHandler authenticationSuccessHandler;

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
                        .requestMatchers("/pcms/reservations/myReservations", "/pcms/reservations/report-return/**").authenticated()
                        .requestMatchers("/pcms/admin/", "/pcms/admin/**").hasRole("ADMIN")
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/pcms/login")
                        .usernameParameter("studentId")
                        .passwordParameter("password")
                        .loginProcessingUrl("/pcms/login")
                        .successHandler(authenticationSuccessHandler)
                        .failureUrl("/pcms/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/pcms/logout")
                        .logoutSuccessUrl("/pcms")
                        .permitAll()
                );
        return http.build();
    }

}
