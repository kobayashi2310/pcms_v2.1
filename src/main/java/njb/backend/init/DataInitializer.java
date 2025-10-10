package njb.backend.init;

import lombok.RequiredArgsConstructor;
import njb.backend.model.User;
import njb.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByStudentId("T22010").isEmpty()) {
            User user = new User();
            user.setStudentId("T22010");
            user.setName("TEST");
            user.setKana("テスト");
            user.setEmail("njb-t22010@example.com");
            user.setHashedPassword(passwordEncoder.encode("password"));
            user.setRole(User.UserRole.STUDENT);
            userRepository.save(user);
        }
    }
}
