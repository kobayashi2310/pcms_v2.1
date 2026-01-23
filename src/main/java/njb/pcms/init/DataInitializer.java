package njb.pcms.init;

import lombok.RequiredArgsConstructor;
import njb.pcms.model.Pc;
import njb.pcms.model.Period;
import njb.pcms.model.User;
import njb.pcms.repository.PcRepository;
import njb.pcms.repository.PeriodRepository;
import njb.pcms.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PeriodRepository periodRepository;
    private final PcRepository pcRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByStudentId("T22010").isEmpty()) {
            User admin = new User();
            admin.setStudentId("ADMIN");
            admin.setName("ADMIN");
            admin.setKana("ADMIN");
            admin.setHashedPassword(passwordEncoder.encode("password"));
            admin.setRole(User.UserRole.ADMIN);
            userRepository.save(admin);

            User user = new User();
            user.setStudentId("T22010");
            user.setName("小林輝流");
            user.setKana("コバヤシ ヒカル");
            user.setHashedPassword(passwordEncoder.encode("password"));
            user.setRole(User.UserRole.STUDENT);
            userRepository.save(user);

            user = new User();
            user.setStudentId("T22017");
            user.setName("山上結史");
            user.setKana("ヤマガミ ユウシ");
            user.setHashedPassword(passwordEncoder.encode("pass"));
            user.setRole(User.UserRole.STUDENT);
            userRepository.save(user);

            List<Period> periods = List.of(
                    new Period(Byte.valueOf("1"), "1限", LocalTime.of(9, 0), LocalTime.of(10, 30)),
                    new Period(Byte.valueOf("2"), "2限", LocalTime.of(10, 40), LocalTime.of(12, 10)),
                    new Period(Byte.valueOf("3"), "3限", LocalTime.of(13, 0), LocalTime.of(14, 30)),
                    new Period(Byte.valueOf("4"), "4限", LocalTime.of(14, 40), LocalTime.of(16, 10)));

            periodRepository.saveAll(periods);

            List<Pc> pcs = List.of(
                    new Pc(null, "PC-001", LocalDateTime.now(), LocalDateTime.now()),
                    new Pc(null, "PC-002", LocalDateTime.now(), LocalDateTime.now()),
                    new Pc(null, "PC-003", LocalDateTime.now(), LocalDateTime.now()),
                    new Pc(null, "PC-004", LocalDateTime.now(), LocalDateTime.now()),
                    new Pc(null, "PC-005", LocalDateTime.now(), LocalDateTime.now()));

            pcRepository.saveAll(pcs);
        }
    }
}
