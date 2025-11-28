package njb.pcms.init;

import lombok.RequiredArgsConstructor;
import njb.pcms.model.User;
import njb.pcms.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class        DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByStudentId("T22010").isEmpty()) {
            User admin = new User();
            admin.setStudentId("ADMIN");
            admin.setName("ADMIN");
            admin.setKana("ADMIN");
            admin.setEmail("admin@localhost");
            admin.setHashedPassword(passwordEncoder.encode("password"));
            admin.setRole(User.UserRole.ADMIN);
            userRepository.save(admin);

            User user = new User();
            user.setStudentId("T22010");
            user.setName("小林輝流");
            user.setKana("コバヤシ ヒカル");
            user.setEmail("njb-t22010@example.com");
            user.setHashedPassword(passwordEncoder.encode("password"));
            user.setRole(User.UserRole.STUDENT);
            userRepository.save(user);

            user = new User();
            user.setStudentId("T22017");
            user.setName("山上結史");
            user.setKana("ヤマガミ ユウシ");
            user.setEmail("njb-t22017@sist.ac.jp");
            user.setHashedPassword(passwordEncoder.encode("pass"));
            user.setRole(User.UserRole.STUDENT);
            userRepository.save(user);
        }
    }
}
