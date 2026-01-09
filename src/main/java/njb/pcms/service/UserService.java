package njb.pcms.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import njb.pcms.dto.BulkStudentRegistrationDto;
import njb.pcms.dto.StudentRegistrationPreviewDto;
import njb.pcms.model.User;
import njb.pcms.repository.UserRepository;
import njb.pcms.util.KanaConverter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KanaConverter kanaConverter;

    public List<User> findByRole(User.UserRole role) {
        return userRepository.findByRole(role);
    }

    public List<User> findByRoleAndStudentIdStartingWith(User.UserRole role, String prefix) {
        return userRepository.findByRoleAndStudentIdStartingWith(role, prefix);
    }

    @Transactional
    public void registerStudent(njb.pcms.dto.StudentRegistrationDto dto) {
        String prefix = "T" + (dto.getAdmissionYear() % 100);
        long count = userRepository.countByRoleAndStudentIdStartingWith(User.UserRole.STUDENT, prefix);
        String studentId = prefix + String.format("%03d", count + 1);

        User user = new User();
        user.setStudentId(studentId);
        user.setName(dto.getName());
        user.setKana(dto.getKana());
        // 初期パスワード
        user.setHashedPassword(passwordEncoder.encode("password"));
        user.setRole(User.UserRole.STUDENT);

        userRepository.save(user);
    }

    @Transactional
    public void registerStudentsBulk(BulkStudentRegistrationDto dto) {
        ParsingResult result = parseAndSortStudentData(dto.getStudentListRaw());
        if (!result.getErrorMessages().isEmpty()) {
            // エラーがある場合は登録しない（コントローラー側で弾く前提だが、念のためログ出力など）
            log.warn("Registration attempted with format errors: {}", result.getErrorMessages());
            // 必要に応じて例外を投げる設計も可
        }

        String prefix = "T" + (dto.getAdmissionYear() % 100);
        long count = userRepository.countByRoleAndStudentIdStartingWith(User.UserRole.STUDENT, prefix);

        for (StudentCandidate candidate : result.getCandidates()) {
            count++;
            String studentId = prefix + String.format("%03d", count);

            User user = new User();
            user.setStudentId(studentId);
            user.setName(candidate.name);
            user.setKana(candidate.kana);
            user.setHashedPassword(passwordEncoder.encode("password"));
            user.setRole(User.UserRole.STUDENT);
            userRepository.save(user);
        }
    }

    public StudentRegistrationPreviewDto previewStudentRegistration(BulkStudentRegistrationDto dto) {
        StudentRegistrationPreviewDto preview = new StudentRegistrationPreviewDto();

        ParsingResult result = parseAndSortStudentData(dto.getStudentListRaw());
        preview.getErrorMessages().addAll(result.getErrorMessages());

        String prefix = "T" + (dto.getAdmissionYear() % 100);
        long count = userRepository.countByRoleAndStudentIdStartingWith(User.UserRole.STUDENT, prefix);

        for (StudentCandidate candidate : result.getCandidates()) {
            count++;
            String studentId = prefix + String.format("%03d", count);

            User user = new User();
            user.setStudentId(studentId);
            user.setName(candidate.name);
            user.setKana(candidate.kana);
            user.setRole(User.UserRole.STUDENT);

            preview.getValidStudents().add(user);
        }

        return preview;
    }

    private ParsingResult parseAndSortStudentData(String rawData) {
        ParsingResult result = new ParsingResult();
        if (rawData == null || rawData.isEmpty()) {
            return result;
        }

        String[] lines = rawData.split("\\r?\\n");
        int lineNumber = 0;
        List<StudentCandidate> tempList = new ArrayList<>();

        for (String line : lines) {
            lineNumber++;
            String trimmedLine = line.trim();
            if (trimmedLine.isEmpty()) {
                continue;
            }

            // 正規化: 全角スペースを半角に、連続するスペースを1つに
            trimmedLine = trimmedLine.replace("　", " ").replaceAll("\\s+", " ");

            // カンマ（,）で分割して氏名とフリガナを取得
            String[] parts = trimmedLine.split(",", 2);
            if (parts.length < 2) {
                result.getErrorMessages().add(lineNumber + "行目のフォーマットが不正です: " + line);
                continue;
            }

            String name = parts[0].trim();
            String kana = parts[1].trim();

            if (name.isEmpty() || kana.isEmpty()) {
                result.getErrorMessages().add(lineNumber + "行目の氏名またはフリガナが空です: " + line);
                continue;
            }

            tempList.add(new StudentCandidate(name, kana));
        }

        // フリガナ順にソート
        tempList.sort(Comparator.comparing(c -> c.kana));
        result.setCandidates(tempList);
        return result;
    }

    @Data
    @AllArgsConstructor
    private static class StudentCandidate {
        private String name;
        private String kana;
    }

    @Data
    private static class ParsingResult {
        private List<StudentCandidate> candidates = new ArrayList<>();
        private List<String> errorMessages = new ArrayList<>();
    }

    public List<User> searchUsers(String query) {
        log.debug("Searching users with query: {}", query);
        if (query.isEmpty()) {
            return List.of();
        }

        String katakana = kanaConverter.hiraganaToKatakana(query);

        return userRepository.findTop10ByStudentIdContainingOrNameContainingOrKanaContaining(
                query, query, katakana);
    }

}
